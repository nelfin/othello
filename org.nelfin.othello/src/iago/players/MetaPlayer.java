package iago.players;

import iago.Board;
import iago.Move;
import iago.features.*;
import iago.history.NamedPosition;
import iago.history.OpeningBook;
import iago.history.UnexploredException;

public class MetaPlayer extends AbstractPlayer{
    public static final int DEFAULT_DEPTH = NegamaxPlayer.DEFAULT_DEPTH;
    private static final long RED_ALERT = 500;
    private enum Stage { BOOK, EARLY, MID, LATE };
    
    private static FeatureSet earlyFeatures = new FeatureSet();
    private static FeatureSet midFeatures = new FeatureSet();
    private static FeatureSet lateFeatures = new FeatureSet();
    static {
        earlyFeatures.add(new LegalMoves(0.87334));
        earlyFeatures.add(new StoneCount(0.00308));
        earlyFeatures.add(new SidePieces(0.01449));
        earlyFeatures.add(new CornerPieces(0.00895)); // C-C-C-COMBO BREAKER
        earlyFeatures.add(new BlockedAdjacent(0.10012));
        earlyFeatures.standardiseWeights();
        midFeatures.add(new LegalMoves(0.33323));
        midFeatures.add(new StoneCount(0.02401));
        midFeatures.add(new SidePieces(0.24816));
        midFeatures.add(new CornerPieces(0.18558));
        midFeatures.add(new BlockedAdjacent(0.20900));
        midFeatures.standardiseWeights();
        lateFeatures.add(new LegalMoves(0.08990));
        lateFeatures.add(new StoneCount(0.05584));
        lateFeatures.add(new SidePieces(0.34081));
        lateFeatures.add(new CornerPieces(0.26868));
        lateFeatures.add(new BlockedAdjacent(0.24475));
        lateFeatures.standardiseWeights();
    }
    NegamaxPlayer earlyGamePlayer;
    NegamaxPlayer midGamePlayer;
    NegamaxPlayer lateGamePlayer;
    Player failsafe;
	OpeningBook openingBook;
	
	private Stage gameStage;
	
	private void instantiateOpeningBook() {
		openingBook = new OpeningBook();
	}
	public MetaPlayer(PlayerType colour) {
	    this(colour, DEFAULT_DEPTH);
    }
    
    public MetaPlayer(PlayerType colour, int depth) {
    	super(colour);
    	gameStage = Stage.BOOK;
        earlyGamePlayer = new NegamaxPlayer(colour, depth);
        midGamePlayer = new NegamaxPlayer(colour, depth);
        lateGamePlayer = new NegamaxPlayer(colour, depth);
        earlyGamePlayer.setFeatureSet(earlyFeatures);
        midGamePlayer.setFeatureSet(midFeatures);
        lateGamePlayer.setFeatureSet(lateFeatures);
        
        failsafe = new GreedyPlayer(colour);
        
		instantiateOpeningBook();
    }
    
    @Override
    public Move chooseMove(Board board, long timeRemaining) {
        if (timeRemaining < RED_ALERT) {
            return failsafe.chooseMove(board);
        } else {
            return this.chooseMove(board);
        }
    }
    
	@Override
	public Move chooseMove(Board board) {
	    // Don't leave the book prematurely
	    if (gameStage != Stage.BOOK) {
	        determineStage(board);
	    }
	    System.out.println("MetaPlayer: game stage is " + gameStage);
        switch (gameStage) {
        case BOOK:
            board.visualise();
            Move m = earlyGamePlayer.chooseMove(board);
            try {
                openingBook.getNextPosition(board.getMostRecentlyPlayedMove());
                openingBook.getNextPosition(m);
            } catch (UnexploredException e) {
                // Drop out at first instance and don't come back
                System.out.println("MetaPlayer: we left the opening book");
                determineStage(board);
                return m;
            }
            // Note: pass through is deliberate
        case EARLY:
            return earlyGamePlayer.chooseMove(board);
        case MID:
            return midGamePlayer.chooseMove(board);
        default:
            return lateGamePlayer.chooseMove(board);
        }
	}
	
    private void determineStage(Board board) {
        // TODO: make this less naive
        int movesLeft = board.movesRemaining();
        int maximumMoves = Board.BOARD_SIZE*Board.BOARD_SIZE - Board.BLOCKED_NUM - 4;
        float fracRemaining = ((float) movesLeft) / ((float) maximumMoves);
        if (fracRemaining > 0.667) {
            gameStage = Stage.EARLY;
        } else if (fracRemaining > 0.333) {
            gameStage = Stage.MID;
        } else {
            gameStage = Stage.LATE;
        }
    }
	
	

}
