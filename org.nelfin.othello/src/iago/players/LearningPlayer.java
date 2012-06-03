package iago.players;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    private static int NUM_MOVES = 92; //100 squares - 4 init - 4 blocked = 92 moves
    private ArrayList<FeatureSet> anyFeatures = new ArrayList<FeatureSet>(NUM_MOVES); 

	public LearningPlayer(PlayerType colour, int depth, FeatureSet features) {
		super(colour);
		negamaxPlayer = new NegamaxPlayer(colour, depth);
		negamaxPlayer.setFeatureSet(features);
	}
	
	public LearningPlayer(PlayerType colour, int depth, String playerID) {
		super(colour);
		this.playerID = playerID;
		negamaxPlayer = new NegamaxPlayer(colour, depth);
		initFeatures();
	}
	
	public LearningPlayer(PlayerType colour, int depth) {
		super(colour);
		negamaxPlayer = new NegamaxPlayer(colour, depth);
		initFeatures();
	}
	
    private String playerID = "JafarLPlates";
	
	private NegamaxPlayer negamaxPlayer;
	
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
	
	public LearningPlayer(PlayerType colour) {
		this(colour, NegamaxPlayer.DEFAULT_DEPTH);
	}
	

	@Override
	public Move chooseMove(Board board) {
		gameHistory.add(new Board(board));
	    setFeatureSet(anyFeatures.get(board.getMovesPlayed()));

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

		//Calculate the change in weight for every feature f
		

			for(int t = 0; t < gameHistory.size()-1; t++){
				double deltaWeight = 0;
				FeatureSet deltaWeights = new FeatureSet();
				FeatureSet thisStageWeightsUsed = anyFeatures.get(t);
				FeatureSet nextStageWeightsUsed = anyFeatures.get(t+1);
				for(Feature f : thisStageWeightsUsed)
				{
					Board xt = gameHistory.get(t);
					double thisStepDelta = deltaJ(xt,thisStageWeightsUsed, f);
					double lambdaTD = 0;
					for(int j = t; j < gameHistory.size()-1; j++){
						Board afterXT = gameHistory.get(t+1);
						double dt = (J(afterXT,nextStageWeightsUsed) - J(xt,thisStageWeightsUsed)); //The temporal difference
						lambdaTD += Math.pow(LAMBDA, j-t) * dt;
					}
					//if(t==gameHistory.size()-1)System.out.println(lambdaTD);
					deltaWeight += LEARNING_RATE * (thisStepDelta * lambdaTD);
					
					Feature deltaFeature = new ErsatzFeature(f);
					deltaFeature.setWeight(deltaWeight);
					deltaWeights.add(deltaFeature);
				}
				anyFeatures.get(t).combine(deltaWeights);
				anyFeatures.get(t).standardiseWeights();

			}
			
			
			

		

		//We're done with this game
		gameHistory.clear();
		
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
	
	/**
	 * Return's the learning player's current feature set (and weightings, of course)
	 * @return The current feature set
	 */
	public FeatureSet getFeatureSet(int i) {
	    return anyFeatures.get(i);
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
