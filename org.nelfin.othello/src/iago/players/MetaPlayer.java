package iago.players;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

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
    
    private static String playerID = "Jafar";
    
    private static int NUM_MOVES = 92; //100 squares - 4 init - 4 blocked = 92 moves
    private ArrayList<FeatureSet> anyFeatures = new ArrayList<FeatureSet>(NUM_MOVES); 
    NegamaxPlayer anyGamePlayer;
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
        
        anyGamePlayer = new NegamaxPlayer(colour, depth);
        initFeatures();
        anyGamePlayer.setFeatureSet(anyFeatures.get(0));
        
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
	    anyGamePlayer.setFeatureSet(anyFeatures.get(board.movesRemaining()));
	    if (gameStage == Stage.BOOK) {
	    	board.visualise();
	    	Move m = anyGamePlayer.chooseMove(board);
	    	try {
	    		openingBook.getNextPosition(board.getMostRecentlyPlayedMove());
	    		openingBook.getNextPosition(m);
	    	} catch (UnexploredException e) {
	    		// Drop out at first instance and don't come back
	    		System.out.println("MetaPlayer: we left the opening book");
	    		determineStage(board);
	    	}
	    	return m;
	    }
	    else return anyGamePlayer.chooseMove(board);
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
	
    public void saveFeatures() {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(playerID + ".nspl"));
			out.writeObject(anyFeatures);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void initFeatures() {
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(playerID + ".nspl"));
			anyFeatures = (ArrayList<FeatureSet>) in.readObject();
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Create default player here
			for (int i = 0; i < NUM_MOVES; i++) {
				FeatureSet currentMove = new FeatureSet();
				currentMove.add(new LegalMoves     (0.2));
				currentMove.add(new StoneCount     (0.2));
				currentMove.add(new SidePieces     (0.2));
				currentMove.add(new CornerPieces   (0.2));
				currentMove.add(new BlockedAdjacent(0.2));
				currentMove.standardiseWeights();
				anyFeatures.add(currentMove);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
