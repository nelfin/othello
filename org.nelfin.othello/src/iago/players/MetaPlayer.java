package iago.players;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import iago.Board;
import iago.Move;
import iago.features.*;

public class MetaPlayer extends LearningPlayer{
	private NegamaxPlayer negamaxPlayer;
	
	static FeatureSet initialWeights = new FeatureSet();
	static {
	    initialWeights.add(new StoneCount(1));
	    initialWeights.add(new SidePieces(0));
	    initialWeights.add(new CornerPieces(0));
	    initialWeights.standardiseWeights();
	}
	FeatureSet currentWeights = new FeatureSet("MetaPlayerLearntWeights");
	
	private final double LEARNING_RATE = 0.1;
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
	
	public MetaPlayer(PlayerType colour, int depth) {
		super(colour);
		negamaxPlayer = new NegamaxPlayer(colour, depth);
		currentWeights = new FeatureSet(initialWeights);
		negamaxPlayer.setFeatureSet(currentWeights); //this will overwrite the features that negamax has as default
	}
	
	public MetaPlayer(PlayerType colour) {
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

	@Override
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

		//We're done with this game
		gameHistory.clear();
		//Modify our feature set


		currentWeights.combine(deltaWeights);
		currentWeights.standardiseWeights();
		
		negamaxPlayer.setFeatureSet(currentWeights);
		negamaxPlayer.getFeatureSet().saveToFile();
		
	}

	@Override
	public FeatureSet getFeatureSet() {
		return negamaxPlayer.getFeatureSet();
	}

	@Override
	public void setFeatureSet(FeatureSet featureSet) {
		negamaxPlayer.setFeatureSet(featureSet);
	}

}
