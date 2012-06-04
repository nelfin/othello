package iago.players;

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
	public StageLearningPlayer(PlayerType colour, int depth, FeatureSet features) {
		super(colour);
		gameStage = 0;
		
		for(int i = 0; i < STAGES; i++){
			nPlayers.add(new LearningPlayer(colour, depth));
		}
	}
	private ArrayList<LearningPlayer> nPlayers = new ArrayList<LearningPlayer>();
	private int gameStage = 0;
	private static int STAGES = 20;
	private static int MOVES = 92;
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
	    
	    int currentStage = determineStage(board);
	    
	    if(gameStage >= STAGES){
			return Move.NO_MOVE;
		}
	    
	    if(gameStage != currentStage)
	    {
	    	nPlayers.get(gameStage).receiveFeedback(0.0);
	    	gameStage = currentStage;
	    }
	    return nPlayers.get(gameStage).chooseMove(board);
		
	}

	/**
	 * Allows a learning player to receive feedback based on it's performance.
	 * @param feedback	A double between 0 and 1 with 0 representing the most negative feedback, and 1 representing the most positive feedback.
	 */
	public void receiveFeedback(double feedback) {
		nPlayers.get(gameStage).receiveFeedback(0.0);
		gameStage = 0;
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
    private FeatureSet getFeatureSet(int s) {
        return nPlayers.get(s).getFeatureSet();
    }
    /**
	 * Sets the learning player's current feature set. The policy iteration should be done in the receiveFeedback function, this is just so we can clone a player
	 * @param featureSet	The FeatureSet that the player plans to take on
	 */
	public void setFeatureSet(FeatureSet featureSet) {
		nPlayers.get(gameStage).setFeatureSet(featureSet);
	}
	
    private int determineStage(Board board) {
        return (int) Math.round(((double)(MOVES-board.movesRemaining())/((double)MOVES) * (STAGES-1)));
    }

    public void showFeatures() {
    	int i = 0;
        for (LearningPlayer n : nPlayers) {
            System.out.print(i);
            System.out.println(n.getFeatureSet());
            i++;
        }
    }
}
