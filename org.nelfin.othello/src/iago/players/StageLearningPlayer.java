package iago.players;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import iago.Board;
import iago.Move;
import iago.features.CornerPieces;
import iago.features.ErsatzFeature;
import iago.features.Feature;
import iago.features.FeatureSet;
import iago.features.LegalMoves;
import iago.features.SidePieces;
import iago.features.StoneCount;
import iago.features.Visibility;
import iago.players.Player.PlayerType;

public class StageLearningPlayer extends AbstractPlayer {
	public StageLearningPlayer(PlayerType colour, int depth, FeatureSet features) {
		super(colour);
		gameStage = Stage.EARLY;
		earlyGamePlayer = new NegamaxPlayer(colour, depth);
		midGamePlayer = new NegamaxPlayer(colour, depth);
		lateGamePlayer = new NegamaxPlayer(colour, depth);
		earlyGamePlayer.setFeatureSet(features);
		midGamePlayer.setFeatureSet(features);
		lateGamePlayer.setFeatureSet(features);
	}
	private enum Stage { EARLY, MID, LATE };
	
	private NegamaxPlayer earlyGamePlayer;
	private NegamaxPlayer midGamePlayer;
	private NegamaxPlayer lateGamePlayer;
	private Stage gameStage;
	
	static FeatureSet initialWeights = new FeatureSet();
	static {
	    initialWeights.add(new LegalMoves(0));
	    initialWeights.add(new StoneCount(1));
	    initialWeights.add(new Visibility(0));
	    initialWeights.add(new SidePieces(0));
	    initialWeights.add(new CornerPieces(0));
	    initialWeights.standardiseWeights();
	}
	FeatureSet currentWeights = new FeatureSet("MetaPlayerLearntWeights");
	
	private final double LEARNING_RATE = 0.1;
	private final double LAMBDA = 0.9;
	private final double RANDOM_MOVE_CHANCE = 0.05;
	private Random randomMoveGenerator = new Random();
	
	double previousFeedback = 0;
	
	ArrayList<Board> gameHistory = new ArrayList<Board>();
	
	/**
	 * The J function in the TD lambda algorithm. J takes a board and a set of weights and returns a board value
	 * @param x		The board
	 * @param w		The weights
	 * @return		The board value
	 */
	private double J(Board x, FeatureSet w){
		return w.score(x, getColour());
	}
	
	/**
	 * The partial derivative of the J function.
	 * @param x		The board
	 * @param w		The weights
	 * @param f		The feature we are deriving with respect to
	 * @return		The board value
	 */
	private double deltaJ(Board x, FeatureSet w, Feature f){
		FeatureSet tempW = new FeatureSet(w);
		final double step = 0.00001; //step is the derivative step
		double before = J(x,w);
		FeatureSet wPlusStep = new FeatureSet();
		
		for(Feature wFeature : tempW){
			if(wFeature.name == f.name){
				wFeature.setWeight(wFeature.getWeight() + step);
			}
			wPlusStep.add(wFeature);
		}
		double after  = J(x,wPlusStep);
		
		return (after-before)/step;
	}
	
	public StageLearningPlayer(PlayerType colour, int depth) {
	    this(colour, depth, initialWeights);
	}
	
	public StageLearningPlayer(PlayerType colour) {
		this(colour, NegamaxPlayer.DEFAULT_DEPTH);
	}
	
	@Override
	public Move chooseMove(Board board) {
		gameHistory.add(new Board(board));
		Move nextMove = Move.NO_MOVE;
		if(randomMoveGenerator.nextDouble() <= RANDOM_MOVE_CHANCE){
			Set<Move> moveSet = board.validMoves(getColour());
			if(moveSet.size() > 0){ //If there are no valid moves, we'll pass
				nextMove = (Move) moveSet.toArray()[randomMoveGenerator.nextInt(moveSet.size())];
			}
		} else {
		    Stage current = determineStage(board);
		    if (current != gameStage) {
		        receiveFeedback(0.0);
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
		return nextMove;
	}

	/**
	 * Allows a learning player to receive feedback based on it's performance.
	 * @param feedback	A double between 0 and 1 with 0 representing the most negative feedback, and 1 representing the most positive feedback.
	 */
	public void receiveFeedback(double feedback) {
	    // TODO we don't really need the feedback because the J function will tell us if
	    // we won/lost on the last step
	    
	    // this.getFeatureSet() should be the current player
		FeatureSet weightsUsed = this.getFeatureSet();
		FeatureSet deltaWeights = new FeatureSet();

		//Calculate the change in weight for every feature f
		for(Feature f : weightsUsed)
		{

			double deltaWeight = 0;
			for(int t = 0; t <= gameHistory.size()-1; t++){
				Board xt = gameHistory.get(t);
				double thisStepDelta = deltaJ(xt,weightsUsed, f);
				double lambdaTD = 0;
				for(int j = t; j < gameHistory.size()-1; j++){
					Board afterXT = gameHistory.get(t+1);
					double dt = (J(afterXT,weightsUsed) - J(xt,weightsUsed)); //The temporal difference
					lambdaTD += Math.pow(LAMBDA, j-t) * dt;
				}
				//if(t==gameHistory.size()-1)System.out.println(lambdaTD);
				deltaWeight += LEARNING_RATE * (thisStepDelta * lambdaTD);


			}
			
			Feature deltaFeature = new ErsatzFeature(f);
			deltaFeature.setWeight(deltaWeight);
			deltaWeights.add(deltaFeature);
			

		}

		//We're done with this game
		gameHistory.clear();
		//Modify our feature set

		weightsUsed = this.getFeatureSet();
		weightsUsed.combine(deltaWeights);
		weightsUsed.standardiseWeights();
		this.setFeatureSet(weightsUsed);
	}

	/**
	 * Return's the learning player's current feature set (and weightings, of course)
	 * @return The current feature set
	 */
	public FeatureSet getFeatureSet() {
	    switch (gameStage) {
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
}
