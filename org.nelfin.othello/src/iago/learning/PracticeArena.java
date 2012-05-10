package iago.learning;

import java.util.HashSet;
import java.util.Random;

import iago.Board;
import iago.DebugFunctions;
import iago.Move;
import iago.players.GreedyPlayer;
import iago.players.MetaPlayer;
import iago.players.NegamaxPlayer;
import iago.players.Player.PlayerType;

public class PracticeArena{
	static final int BLOCKED_COUNT=4; //TODO: move this
	static final int GAME_REPEATS=5;
	public static void main(String[] args)
	{
		GreedyPlayer blackOpponent = new GreedyPlayer(PlayerType.BLACK);
		GreedyPlayer whiteOpponent = new GreedyPlayer(PlayerType.WHITE);
		//This is the learning player. They could both learn, but it's easy to reference them this way
		NegamaxPlayer whiteLearner = new NegamaxPlayer(PlayerType.WHITE); 
		NegamaxPlayer blackLearner = new NegamaxPlayer(PlayerType.BLACK); 

		Board board = new Board();
		double feedback = 0;
		
		for(int i = 0; i < GAME_REPEATS;i++)
		{

			int side = 0;
			NegamaxPlayer learner;
			GreedyPlayer opponent;
			Board initialBoard = generateRandomBoard();
			//We want the player to play from both sides
			for(side = 0; side <= 1; side++) //side==0 means Learner is playing white
			{
				System.out.println("Playing game "+(i+1)+"/"+GAME_REPEATS+((side==0)?"":" reversed"));

				//Set up a game
				board = initialBoard;
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
				while(!nextMove.equals(new Move(-1,-1)))
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
				}
				int boardScore = board.scoreBoard(PlayerType.WHITE);
				
				boolean whiteWins = boardScore > 0;
				boolean tie = boardScore == 0;
				double thisGameFeedback = 0.0;
				
				
				//Allocate points
				if(whiteWins && side==0 && !tie){ //Learner won while white
					thisGameFeedback = 1.0 / (2.0 * GAME_REPEATS); //We only give 0.5 for a win, as we have to play from both sides. 1 means we won both sides.
				}
				if(!whiteWins && side==1 && !tie){ //Learner won while black
					thisGameFeedback = 1.0 / (2.0 * GAME_REPEATS); 
				}
				if(tie){
					thisGameFeedback = 0.5 / (2.0 * GAME_REPEATS);
				}
				feedback += thisGameFeedback;
				System.out.println(boardScore);
				System.out.println(thisGameFeedback);
			}
		}
		System.out.println("Final score: "+feedback+" / 1");

	}
	private static Board generateRandomBoard()
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
		
		Board board = new Board(DebugFunctions.charArrayToBoardString(randomBoardRepresentation));
		return board;
	}

}
