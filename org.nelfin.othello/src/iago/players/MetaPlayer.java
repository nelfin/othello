package iago.players;

import iago.Board;
import iago.Move;

public class MetaPlayer extends AbstractPlayer{
    public static final int DEFAULT_DEPTH = NegamaxPlayer.DEFAULT_DEPTH;
    private enum Stage { EARLY, MID, LATE };
    
    Player earlyGamePlayer;
    Player midGamePlayer;
    Player lateGamePlayer;
	OpeningBookPlayer openingBookPlayer;
	
	private Stage gameStage;
	
	private void instantiateOpeningBookPlayer(PlayerType colour){
		openingBookPlayer = new OpeningBookPlayer(colour);
	}
	public MetaPlayer(PlayerType colour) {
	    this(colour, DEFAULT_DEPTH);
    }
    
    public MetaPlayer(PlayerType colour, int depth) {
    	super(colour);
    	gameStage = Stage.EARLY;
        earlyGamePlayer = new NegamaxPlayer(colour, depth);
        midGamePlayer = new NegamaxPlayer(colour, depth);
        lateGamePlayer = new NegamaxPlayer(colour, depth);
		instantiateOpeningBookPlayer(colour);
    }

	@Override
	public Move chooseMove(Board board) {
	    determineStage(board);
	    System.out.println("MetaPlayer: game stage is " + gameStage);
		Move openingMove = openingBookPlayer.chooseMove(board);
		if (openingMove != Move.NO_MOVE) { //Also make sure that it's a legal move (blocked spaces)
			return openingMove;
		} else {
		    switch (gameStage) {
		    case EARLY:
		        return earlyGamePlayer.chooseMove(board);
		    case MID:
		        return midGamePlayer.chooseMove(board);
		    default:
		        return lateGamePlayer.chooseMove(board);
		    }
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
