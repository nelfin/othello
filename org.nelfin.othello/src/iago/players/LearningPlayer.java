package iago.players;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import iago.Board;
import iago.Move;
import iago.features.BlockedAdjacent;
import iago.features.CornerPieces;
import iago.features.ErsatzFeature;
import iago.features.Feature;
import iago.features.FeatureSet;
import iago.features.LegalMoves;
import iago.features.SidePieces;
import iago.features.StoneCount;
import iago.features.Visibility;
import iago.players.Player.PlayerType;

public class LearningPlayer extends AbstractPlayer {
	public LearningPlayer(PlayerType colour, int depth, FeatureSet features) {
		super(colour);
		negamaxPlayer = new NegamaxPlayer(colour, depth);
		negamaxPlayer.setFeatureSet(features);
	}
	
	private NegamaxPlayer negamaxPlayer;
	
	static FeatureSet initialWeights = new FeatureSet();
	static {
		initialWeights.add(new LegalMoves(0.13));
	    //initialWeights.add(new StoneCount(0.37));
	    //initialWeights.add(new Visibility(0.032));
	    initialWeights.add(new SidePieces(0.3));
	    initialWeights.add(new CornerPieces(0.16));
	    initialWeights.add(new BlockedAdjacent(0.16));
	    initialWeights.standardiseWeights();
	}
	FeatureSet currentWeights = new FeatureSet("MetaPlayerLearntWeights");
	
	private final double LEARNING_RATE = 0.05;
	private final double LAMBDA = 0.9;
	private final double RANDOM_MOVE_CHANCE = 0.01;
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
		PlayerType colour = negamaxPlayer.getColour();
//		boolean gameOver = (x.validMoves(colour).size() == 0) && (x.validMoves(colour.getOpponent()).size() == 0);
//		boolean weHaveMorePoints = x.scoreBoard(colour) > 0;
//		
//		if (gameOver && weHaveMorePoints) return 1;
//		if (gameOver && !weHaveMorePoints) return 0;
		return w.score(x, colour);
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
	
	public LearningPlayer(PlayerType colour, int depth) {
		super(colour);
		negamaxPlayer = new NegamaxPlayer(colour, depth);
		currentWeights = new FeatureSet(initialWeights);
		negamaxPlayer.setFeatureSet(currentWeights); //this will overwrite the features that negamax has as default
	}
	
	
	
	public LearningPlayer(PlayerType colour) {
		this(colour, NegamaxPlayer.DEFAULT_DEPTH);
	}
	

	@Override
	public Move chooseMove(Board board) {
		gameHistory.add(new Board(board));
		Move nextMove = new Move(-1,-1);
		if(randomMoveGenerator.nextDouble() <= RANDOM_MOVE_CHANCE){
			Set<Move> moveSet = board.validMoves(getColour());
			if(moveSet.size() > 0){ //If there are no valid moves, we'll pass
				nextMove = (Move) moveSet.toArray()[randomMoveGenerator.nextInt(moveSet.size())];
			}
		}else{
			nextMove =  negamaxPlayer.chooseMove(board);
		}
		return nextMove;
	}

	/**
	 * Allows a learning player to receive feedback based on it's performance.
	 * @param feedback	A double between 0 and 1 with 0 representing the most negative feedback, and 1 representing the most positive feedback.
	 */
	public void receiveFeedback(double feedback) { //TODO we don't really need the feedback because the J function will tell us if we won/lost on the last step
		FeatureSet weightsUsed = negamaxPlayer.getFeatureSet();
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
		//Graph code
//		for(int t = 0; t <= gameHistory.size()-1; t++){
//			Board xt = gameHistory.get(t);
//			System.out.println(t+","+J(xt,weightsUsed));
//			
//		}

		//We're done with this game
		gameHistory.clear();
		//Modify our feature set


		currentWeights.combine(deltaWeights);
		currentWeights.standardiseWeights();
		
		negamaxPlayer.setFeatureSet(currentWeights);
		negamaxPlayer.getFeatureSet().saveToFile();
		
	}

	/**
	 * Return's the learning player's current feature set (and weightings, of course)
	 * @return The current feature set
	 */
	public FeatureSet getFeatureSet() {
		return negamaxPlayer.getFeatureSet();
	}

	/**
	 * Sets the learning player's current feature set. The policy iteration should be done in the receiveFeedback function, this is just so we can clone a player
	 * @param featureSet	The FeatureSet that the player plans to take on
	 */
	public void setFeatureSet(FeatureSet featureSet) {
		negamaxPlayer.setFeatureSet(featureSet);
	}
}
