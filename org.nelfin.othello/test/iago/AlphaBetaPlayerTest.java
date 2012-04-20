package iago;

import static org.junit.Assert.*;
import org.junit.Test;

public class AlphaBetaPlayerTest {

	@Test
	public void testChooseMoveFlipLine() {
		AlphaBetaPlayer greedyPlayer = new AlphaBetaPlayer(Player.PlayerType.WHITE, 1);
		Board.BoardState[][] testBoardData = DebugFunctions.makeSolidBoardStateArray(Board.BoardState.EMPTY);
		testBoardData[0][0] = Board.BoardState.WHITE;
		testBoardData[0][1] = Board.BoardState.BLACK;
		testBoardData[0][2] = Board.BoardState.BLACK;
		Board testBoard = new Board(testBoardData, 3);

		assertEquals(greedyPlayer.chooseMove(testBoard), new Move(0,3));
	}
	@Test
	public void testOnlyAvailableMove() {
		AlphaBetaPlayer greedyPlayer = new AlphaBetaPlayer(Player.PlayerType.WHITE, 1);
		Board.BoardState[][] testBoardData = DebugFunctions.makeSolidBoardStateArray(Board.BoardState.BLOCKED);
		Move correctMove = new Move(4,2);
		testBoardData[correctMove.x][correctMove.y] = Board.BoardState.EMPTY;
		testBoardData[correctMove.x-1][correctMove.y] = Board.BoardState.BLACK;
		testBoardData[correctMove.x-2][correctMove.y] = Board.BoardState.WHITE;

		Board testBoard = new Board(testBoardData, 0);
		assertEquals(greedyPlayer.chooseMove(testBoard), correctMove);
	}
	
	@Test
	public void testChooseMoveBestLine() {
		AlphaBetaPlayer greedyPlayer = new AlphaBetaPlayer(Player.PlayerType.WHITE, 1);
		Board.BoardState[][] testBoardData = DebugFunctions.makeSolidBoardStateArray(Board.BoardState.EMPTY);

		testBoardData[0][0] = Board.BoardState.WHITE;
		testBoardData[0][1] = Board.BoardState.BLACK;
		testBoardData[0][2] = Board.BoardState.BLACK;
		
		testBoardData[3][0] = Board.BoardState.WHITE;
		testBoardData[3][1] = Board.BoardState.BLACK;
		testBoardData[3][2] = Board.BoardState.BLACK;
		testBoardData[3][3] = Board.BoardState.BLACK;
		Board testBoard = new Board(testBoardData, 7);

		assertEquals(greedyPlayer.chooseMove(testBoard), new Move(3,4));
	}

	@Test
	public void testSetSearchDepth() {
		AlphaBetaPlayer testPlayer = new AlphaBetaPlayer(Player.PlayerType.WHITE);
		testPlayer.setSearchDepth(2);
		assertEquals(testPlayer.getSearchDepth(), 2);

	}

	@Test
	public void testGetSearchDepth() {
		AlphaBetaPlayer testPlayer = new AlphaBetaPlayer(Player.PlayerType.WHITE, 3);
		assertEquals(testPlayer.getSearchDepth(), 3);
	}

}
