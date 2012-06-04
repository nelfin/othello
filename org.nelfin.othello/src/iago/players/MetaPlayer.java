package iago.players;

import iago.Board;
import iago.Move;
import iago.features.*;
import iago.history.OpeningBook;
import iago.history.UnexploredException;

public class MetaPlayer extends AbstractPlayer{
    public static final int DEFAULT_DEPTH = NegamaxPlayer.DEFAULT_DEPTH;
    private enum Stage { BOOK, EARLY, MID, LATE };
    
    private static FeatureSet earlyFeatures = new FeatureSet();
    private static FeatureSet midFeatures = new FeatureSet();
    private static FeatureSet lateFeatures = new FeatureSet();
    static {
        earlyFeatures.add(new StoneCount(1));
        earlyFeatures.add(new LegalMoves(10));
        earlyFeatures.add(new BlockedAdjacent(8));
        earlyFeatures.standardiseWeights();
        midFeatures.add(new StoneCount(2));
        midFeatures.add(new LegalMoves(15));
        midFeatures.add(new BlockedAdjacent(10));
        midFeatures.add(new SidePieces(7));
        midFeatures.add(new CornerPieces(7));
        midFeatures.standardiseWeights();
        lateFeatures.add(new StoneCount(10));
        lateFeatures.add(new LegalMoves(5));
        lateFeatures.add(new BlockedAdjacent(5));
        lateFeatures.standardiseWeights();
    }
    NegamaxPlayer earlyGamePlayer;
    NegamaxPlayer midGamePlayer;
    NegamaxPlayer lateGamePlayer;
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
		instantiateOpeningBook();
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
            Move openingMove;
            board.visualise();
            try {
                openingMove = openingBook.getNextOpeningMove(board.getMostRecentlyPlayedMove());
                return openingMove;
            } catch (UnexploredException e) {
                // Drop out at first instance and don't come back
                System.out.println("MetaPlayer: we left the opening book");
                determineStage(board);
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
