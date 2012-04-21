package iago;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import iago.Player.PlayerType;



public class BoardTest {

	@Test
	public void testValidMoves() {
	    Board testBoard = new Board("bw........" +
	                                "ww........" +
	                                ".........." +
	                                ".........." +
	                                "....b....." +
	                                ".........." +
	                                ".........." +
	                                ".........." +
	                                ".........." +
	                                "..........");
		Set<Move> possibleMoves = testBoard.validMoves(PlayerType.BLACK);
		assertEquals(possibleMoves.size(),3);
		
		Set<Move> correctPossibleMoves = new HashSet<Move>();
		correctPossibleMoves.add(new Move(2,2));
		correctPossibleMoves.add(new Move(2,0));
		correctPossibleMoves.add(new Move(0,2));
		assertEquals(possibleMoves,correctPossibleMoves);
	}

	@Test
	public void testScoreMove() {
	    Board testBoard = new Board("b........." +
	                                "w........." +
	                                "b........." +
	                                "wb........" +
	                                "w........." +
	                                ".........." +
	                                ".........." +
	                                ".........." +
	                                ".........." +
	                                "..........");
		assertEquals(testBoard.scoreMove(new Move(0,5), PlayerType.BLACK),2);
		assertEquals(testBoard.scoreMove(new Move(2,3), PlayerType.WHITE),1);
	}


	@Test
	public void testScoreBoard() {
		char[][] testBoardData = DebugFunctions.makeSolidBoardCharArray('.');
		Random generator = new Random();
		int seed = generator.nextInt();
		Random seededGenerator = new Random(generator.nextInt());
		for(int i = 0; i < seededGenerator.nextInt(50); i++)
		{
			int x = seededGenerator.nextInt(10);
			int y = seededGenerator.nextInt(10);
			boolean player = seededGenerator.nextBoolean();
			testBoardData[x][y] = player?'w':'b';
		}
		Board testBoard = new Board(DebugFunctions.charArrayToBoardString(testBoardData));
		//We're going to fail
		if(testBoard.scoreBoard(PlayerType.BLACK) != -testBoard.scoreBoard(PlayerType.WHITE))
		{
			System.out.println("[BoardTest] testScoreBoard: Failure with seed "+seed); //Print the seed so the error is reproducible
		}
		assertEquals(testBoard.scoreBoard(PlayerType.BLACK),-testBoard.scoreBoard(PlayerType.WHITE));
	}

	@Test
	public void testApply() {
	    Board testBoard = new Board(".........." +
	                                ".........." +
	                                ".........." +
	                                ".........." +
	                                "....bw...." +
	                                "....wb...." +
	                                ".........." +
	                                ".........." +
	                                ".........." +
	                                "..........");
		Board sameBoard = testBoard.apply(new Move(0,0), PlayerType.BLACK, false);
		for(int x = 0; x < Board.BOARD_SIZE; x++)
		{
			for(int y = 0; y < Board.BOARD_SIZE; y++)
			{
				assertEquals(testBoard.get(x, y),sameBoard.get(x,y));
			}
		}
	}

}
