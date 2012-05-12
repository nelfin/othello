package iago.learning;

import java.util.HashSet;
import java.util.Random;

import iago.Board;
import iago.DebugFunctions;
import iago.Move;
import iago.players.AlphaBetaPlayer;
import iago.players.GreedyPlayer;
import iago.players.MetaPlayer;
import iago.players.NegamaxPlayer;
import iago.players.Player.PlayerType;

public class PracticeArena{
	static final int BLOCKED_COUNT=4; //TODO: move this
	static final int LEARNING_ITERATIONS=15000;
	public static void main(String[] args)
	{
		AlphaBetaPlayer blackOpponent = new AlphaBetaPlayer(PlayerType.BLACK,3);
		AlphaBetaPlayer whiteOpponent = new AlphaBetaPlayer(PlayerType.WHITE,3);
		//This is the learning player. They could both learn, but it's easy to reference them this way
		MetaPlayer whiteLearner = new MetaPlayer(PlayerType.WHITE,3); 
		MetaPlayer blackLearner = new MetaPlayer(PlayerType.BLACK,3); 
		
		
		
		for(int a = 0; a < LEARNING_ITERATIONS; a++){
			double feedback = 0;
			System.out.println("=====================");
			System.out.println("Learning iteration "+(a+1));
			System.out.println("=====================");

			int side = 0;
			MetaPlayer learner;
			AlphaBetaPlayer opponent;
			String initialBoardRepresentation = generateRandomBoard();
			//We want the player to play from both sides
			for(side = 0; side <= 1; side++) //side==0 means Learner is playing white
			{
				System.out.println("Playing game "+(a+1)+"/"+LEARNING_ITERATIONS+((side==0)?"":" reversed"));
				
				//Set up a game
				Board board = new Board(initialBoardRepresentation);
				boolean learnerTurn;
				if(side==0){
					learner = whiteLearner;
					opponent = blackOpponent;
					learnerTurn = true;
				}else{
					learner = blackLearner;
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
						nextMove = learner.chooseMove(board);
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
					thisGameFeedback = 1.0;
				}
				if(!whiteWins && side==1 && !tie){ //Learner won while black
					thisGameFeedback = 1.0; 
				}
				if(tie){
					thisGameFeedback = 0.5;
				}
				feedback += thisGameFeedback;
				System.out.println("Feedback: "+thisGameFeedback);
			}
		
			//Improve our feature weights (maybe)
			whiteLearner.receiveFeedback(feedback);
			//Both players can learn, and we can check out the weights for playing from both sides separately
			blackLearner.receiveFeedback(feedback);
			System.out.println("White: "+whiteLearner.getFeatureSet());
			System.out.println("Black: "+blackLearner.getFeatureSet());
		}
		
		System.out.println("Done");

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
