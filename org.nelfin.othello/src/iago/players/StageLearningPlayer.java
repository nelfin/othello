package iago.players;

import iago.Board;
import iago.Move;
import iago.features.BlockedAdjacent;
import iago.features.CornerPieces;
import iago.features.FeatureSet;
import iago.features.LegalMoves;
import iago.features.SidePieces;
import iago.features.StoneCount;

public class StageLearningPlayer extends AbstractPlayer {
	public StageLearningPlayer(PlayerType colour, int depth, FeatureSet features) {
		super(colour);
		gameStage = Stage.EARLY;
		earlyGamePlayer = new LearningPlayer(colour, depth);
		midGamePlayer = new LearningPlayer(colour, depth);
		lateGamePlayer = new LearningPlayer(colour, depth);
		earlyGamePlayer.setFeatureSet(features);
		midGamePlayer.setFeatureSet(features);
		lateGamePlayer.setFeatureSet(features);
	}
	private enum Stage { EARLY, MID, LATE };
	
	private LearningPlayer earlyGamePlayer;
	private LearningPlayer midGamePlayer;
	private LearningPlayer lateGamePlayer;
	private Stage gameStage;
	
	static FeatureSet initialWeights = new FeatureSet();
	static {
	    initialWeights.add(new LegalMoves(0));
	    initialWeights.add(new StoneCount(1));
	    initialWeights.add(new BlockedAdjacent(0));
	    initialWeights.add(new SidePieces(0));
	    initialWeights.add(new CornerPieces(0));
	    initialWeights.standardiseWeights();
	}
	FeatureSet currentWeights = new FeatureSet("MetaPlayerLearntWeights");
	
	
	double previousFeedback = 0;
	
	
	
	
	public StageLearningPlayer(PlayerType colour, int depth) {
	    this(colour, depth, initialWeights);
	}
	
	public StageLearningPlayer(PlayerType colour) {
		this(colour, NegamaxPlayer.DEFAULT_DEPTH);
	}
	
	@Override
	public Move chooseMove(Board board) {
		Move nextMove = Move.NO_MOVE;
	    Stage current = determineStage(board);
	    
	    if (current != gameStage) {
	    	switch (gameStage) {
			    case EARLY:
			        earlyGamePlayer.receiveFeedback(0.0);
			    case MID:
			        midGamePlayer.receiveFeedback(0.0);
			    default:
			        lateGamePlayer.receiveFeedback(0.0);
	    	}
	        gameStage = current;
	    }
	    
	    switch (gameStage) {
	    case EARLY:
	        return earlyGamePlayer.chooseMove(board);
	    case MID:
	        return midGamePlayer.chooseMove(board);
	    default:
	        return lateGamePlayer.chooseMove(board);
	    }
		
	}

	/**
	 * Allows a learning player to receive feedback based on it's performance.
	 * @param feedback	A double between 0 and 1 with 0 representing the most negative feedback, and 1 representing the most positive feedback.
	 */
	public void receiveFeedback(double feedback) {
    	switch (gameStage) {
		    case EARLY:
		        earlyGamePlayer.receiveFeedback(0.0);
		    case MID:
		        midGamePlayer.receiveFeedback(0.0);
		    default:
		        lateGamePlayer.receiveFeedback(0.0);
    	}
	}

	/**
	 * Return's the learning player's current feature set (and weightings, of course)
	 * @return The current feature set
	 */
	public FeatureSet getFeatureSet() {
	    return getFeatureSet(gameStage);
	}
    
	/**
     * Return's the learning player's current feature set (and weightings, of course)
     * @return The current feature set
     */
    private FeatureSet getFeatureSet(Stage s) {
        switch (s) {
        case EARLY:
            return earlyGamePlayer.getFeatureSet();
        case MID:
            return midGamePlayer.getFeatureSet();
        default:
            return lateGamePlayer.getFeatureSet();
        }
    }
    /**
	 * Sets the learning player's current feature set. The policy iteration should be done in the receiveFeedback function, this is just so we can clone a player
	 * @param featureSet	The FeatureSet that the player plans to take on
	 */
	public void setFeatureSet(FeatureSet featureSet) {
	    switch (gameStage) {
	    case EARLY:
	        earlyGamePlayer.setFeatureSet(featureSet);
	        break;
	    case MID:
	        midGamePlayer.setFeatureSet(featureSet);
	        break;
	    case LATE:
	        lateGamePlayer.setFeatureSet(featureSet);
	        break;
	    }
	}
	
    private Stage determineStage(Board board) {
        // TODO: make this less naive
        int movesLeft = board.movesRemaining();
        int maximumMoves = Board.BOARD_SIZE*Board.BOARD_SIZE - Board.BLOCKED_NUM - 4;
        float fracRemaining = ((float) movesLeft) / ((float) maximumMoves);
        if (fracRemaining > 0.667) {
            return Stage.EARLY;
        } else if (fracRemaining > 0.333) {
            return Stage.MID;
        } else {
            return Stage.LATE;
        }
    }

    public void showFeatures() {
        for (Stage f : Stage.values()) {
            System.out.println(getColour() + ": " + f + " " + getFeatureSet(f).toString());
        }
    }
}
