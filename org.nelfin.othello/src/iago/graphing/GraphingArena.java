package iago.graphing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import iago.Board;
import iago.DebugFunctions;
import iago.Move;
import iago.features.BlockedAdjacent;
import iago.features.FeatureSet;
import iago.features.LegalMoves;
import iago.features.SidePieces;
import iago.features.StoneCount;
import iago.players.AlphaBetaPlayer;
import iago.players.GreedyPlayer;
import iago.players.MetaPlayer;
import iago.players.NegamaxPlayer;
import iago.players.Player.PlayerType;

public class GraphingArena{
	static final int BLOCKED_COUNT=4; //TODO: move this
	static final int GRAPHING_ITERATIONS=5;
	static final String GRAPHING_DIRECTORY = "Graphs";
	static final double WEIGHT_RESOLUTION = 0.01;
	public static void main(String[] args)
	{
		AlphaBetaPlayer blackOpponent = new AlphaBetaPlayer(PlayerType.BLACK,2);
		AlphaBetaPlayer whiteOpponent = new AlphaBetaPlayer(PlayerType.WHITE,2);
		//This is the variable player
		NegamaxPlayer whiteVariable = new NegamaxPlayer(PlayerType.WHITE,2); 
		NegamaxPlayer blackVariable = new NegamaxPlayer(PlayerType.BLACK,2);
		
		ArrayList<FeatureSet> featuresToTry = new ArrayList<FeatureSet>();
		for(double stoneCountWeight = 0; stoneCountWeight <= 1; stoneCountWeight += WEIGHT_RESOLUTION){
			for(double legalMovesWeight = 0; legalMovesWeight <= (1-stoneCountWeight); legalMovesWeight += WEIGHT_RESOLUTION){
				double sidePiecesWeight = 1 - (stoneCountWeight + legalMovesWeight);
				BlockedAdjacent ba = new BlockedAdjacent(stoneCountWeight);
				StoneCount sc = new StoneCount(stoneCountWeight);
				LegalMoves lm = new LegalMoves(legalMovesWeight);
				SidePieces sp = new SidePieces(sidePiecesWeight);
				FeatureSet fs = new FeatureSet();
				fs.add(sc);
				fs.add(lm);
				fs.add(sp);
				featuresToTry.add(fs);
				
			}
		}
		
		//Start our graph file
		try{
			/**<META CODE>**/
			//Make a directory for the logs if it doesn't exist
			try{new File(GRAPHING_DIRECTORY).mkdir();}
			catch (Exception e){//Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}
		    // Create file 
		    FileWriter graphStream = new FileWriter(GRAPHING_DIRECTORY+"/triangle.csv");
		    BufferedWriter graphWriter = new BufferedWriter(graphStream);
		    graphWriter.write("StoneCount, LegalMoves, SideCount ,Win/Loss\n");
		    /**</META CODE>**/
		    
			for(FeatureSet currentFeatureSet : featuresToTry){
				double feedback = 0;

				for(int a = 0; a < GRAPHING_ITERATIONS; a++){
					System.out.println("=====================");
					System.out.println("Graphing iteration "+(a+1));
					System.out.println("=====================");
					//Set the feature set to the set we're trying
					whiteVariable.setFeatureSet(currentFeatureSet);
					blackVariable.setFeatureSet(currentFeatureSet);
					
					
					int side = 0;
					NegamaxPlayer variablePlayer;
					AlphaBetaPlayer opponent;
					String initialBoardRepresentation = generateRandomBoard();
					//We want the player to play from both sides
					for(side = 0; side <= 1; side++) //side==0 means Learner is playing white
					{
						System.out.println("Playing game "+(a+1)+"/"+GRAPHING_ITERATIONS+((side==0)?"":" reversed"));
						
						//Set up a game
						Board board = new Board(initialBoardRepresentation);
						boolean learnerTurn;
						if(side==0){
							variablePlayer = whiteVariable;
							opponent = blackOpponent;
							learnerTurn = true;
						}else{
							variablePlayer = blackVariable;
							opponent = whiteOpponent;
							learnerTurn = false;
						}
						Move nextMove = new Move(0,0);
						
						//Start playing the game
						int consecutivePasses = 0;
						while(consecutivePasses < 2)
						{		
							if(!learnerTurn)
							{
								nextMove = opponent.chooseMove(board);
								//apply the move
								board.apply(nextMove, (side==0)?PlayerType.BLACK:PlayerType.WHITE, true);
		
							}else{
								nextMove = variablePlayer.chooseMove(board);
								//apply the move
								board.apply(nextMove, (side==0)?PlayerType.WHITE:PlayerType.BLACK, true);
							}
							learnerTurn = !learnerTurn;
							
							//For figuring out if the game is over
							if(nextMove.equals(new Move(-1,-1))){
								consecutivePasses++;
							}else{
								consecutivePasses = 0;
							}
						}
						int boardScore = board.scoreBoard(PlayerType.WHITE);
						
						boolean whiteWins = boardScore > 0;
						boolean tie = boardScore == 0;
						double thisGameFeedback = 0.0;
						
						
						//Allocate points
						if(whiteWins && side==0 && !tie){ //Learner won while white
							thisGameFeedback = 1.0 / 2; // div 2 since we're going to play from both sides
						}
						if(!whiteWins && side==1 && !tie){ //Learner won while black
							thisGameFeedback = 1.0 / 2; 
						}
						if(tie){
							thisGameFeedback = 0.5 / 2;
						}
						feedback += thisGameFeedback / GRAPHING_ITERATIONS;
					}
					
					
					//Learner
					//Improve our feature weights
					//whiteLearner.receiveFeedback(feedback);
					//Both players can learn, and we can check out the weights for playing from both sides separately
					//blackLearner.receiveFeedback(feedback);
					//Opponent learns too
					//whiteOpponent.receiveFeedback(feedback);
					//blackOpponent.receiveFeedback(feedback);
					
					//System.out.println("White: "+whiteLearner.getFeatureSet());
					//System.out.println("Black: "+blackLearner.getFeatureSet());
					
					
					
					
				}
				/**<META CODE>**/
				graphWriter.write(whiteVariable.getFeatureSet().get(0).getWeight()+","+whiteVariable.getFeatureSet().get(1).getWeight()+","+whiteVariable.getFeatureSet().get(2).getWeight()+","+ feedback+"\n");//a bit dodgy - we don't really have guarantees on the get method (it's a set)
			    /**</META CODE>**/
			}
			
		System.out.println("Done");
		
	    //Close the output stream
		graphWriter.close();
	    }catch (Exception e){//Catch exception if any
	    	  System.err.println("Error: " + e.getMessage());
	    }
	}

	
	private static String generateRandomBoard()
	{
		char[][] randomBoardRepresentation = DebugFunctions.makeSolidBoardCharArray('.');

		
		//Block positions
		HashSet<int[]> possibleBlockedPositions = new HashSet<int[]>();
		HashSet<int[]> impossibleBlockedPositions = new HashSet<int[]>();
		impossibleBlockedPositions.add(new int[]{4,4});
		impossibleBlockedPositions.add(new int[]{4,5});
		impossibleBlockedPositions.add(new int[]{5,4});
		impossibleBlockedPositions.add(new int[]{5,5});

		for(int x = 0; x < Board.BOARD_SIZE; x++){
			for(int y = 0; y < Board.BOARD_SIZE; y++){
				//Make a list of positions we might block
				int[] thisPosition = new int[]{x,y};
				if(!impossibleBlockedPositions.contains(thisPosition))
				{
					possibleBlockedPositions.add(thisPosition);
				}
			}
		}
		for(int i = 0; i < BLOCKED_COUNT;i++)
		{
			Random random = new Random();
			int blockIndex = random.nextInt(possibleBlockedPositions.size());
			int[] positionToBlock = (int[]) possibleBlockedPositions.toArray()[blockIndex];
			possibleBlockedPositions.remove(positionToBlock); //We don't want to choose this one twice
			randomBoardRepresentation[positionToBlock[1]][positionToBlock[0]] = '*';
		}
		//Add the starting positions
		randomBoardRepresentation[4][4] = 'w';
		randomBoardRepresentation[5][5] = 'w';
		randomBoardRepresentation[4][5] = 'b';
		randomBoardRepresentation[5][4] = 'b';
		
		
		return (DebugFunctions.charArrayToBoardString(randomBoardRepresentation));
	}

}
