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
import iago.features.BlockedAdjacent;
import iago.features.CornerPieces;
import iago.features.FeatureSet;
import iago.features.LegalMoves;
import iago.features.SidePieces;
import iago.features.StoneCount;

public class StageLearningPlayer extends AbstractPlayer {
	
	public StageLearningPlayer(PlayerType colour, int depth, String playerID) {
		super(colour);
		this.playerID = playerID;
		anyGamePlayer = new LearningPlayer(colour, depth);
		initFeatures();
	}
	
	public StageLearningPlayer(PlayerType colour, int depth) {
		super(colour);
		anyGamePlayer = new LearningPlayer(colour, depth);
		initFeatures();
	}
	
    private String playerID = "Jafar";
    
    private static int NUM_MOVES = 92; //100 squares - 4 init - 4 blocked = 92 moves
    private ArrayList<FeatureSet> anyFeatures = new ArrayList<FeatureSet>(NUM_MOVES); 

	private LearningPlayer anyGamePlayer;	
	
	public StageLearningPlayer(PlayerType colour) {
		this(colour, NegamaxPlayer.DEFAULT_DEPTH);
	}
	
	@Override
	public Move chooseMove(Board board) {
		if (board.getMovesPlayed() == NUM_MOVES) return Move.NO_MOVE;
	    //anyGamePlayer.receiveFeedback(0.0);
	    anyGamePlayer.setFeatureSet(anyFeatures.get(board.getMovesPlayed()));
	    return anyGamePlayer.chooseMove(board);
	}

	/**
	 * Allows a learning player to receive feedback based on it's performance.
	 * @param feedback	A double between 0 and 1 with 0 representing the most negative feedback, and 1 representing the most positive feedback.
	 */
	public void receiveFeedback(double feedback) {
		anyGamePlayer.receiveFeedback(0.0);
	}

	/**
	 * Return's the learning player's current feature set (and weightings, of course)
	 * @return The current feature set
	 */
	public FeatureSet getFeatureSet(int i) {
	    return anyFeatures.get(i);
	}
 
    /**
	 * Sets the learning player's current feature set. The policy iteration should be done in the receiveFeedback function, this is just so we can clone a player
	 * @param featureSet	The FeatureSet that the player plans to take on
	 */
	public void setFeatureSet(FeatureSet featureSet, int stage) {
		anyFeatures.set(stage, featureSet);
	}

    public void showFeatures() {
    	for (int i = 0; i < NUM_MOVES; i++) {
            System.out.println(getColour() + ": " + i + " " + getFeatureSet(i).toString());
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
