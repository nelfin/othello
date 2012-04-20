package iago;
import static org.junit.Assert.*;
import org.junit.Test;

public class AlphaBetaPlayerTest extends PlayerTest {
	protected void setUp() {
		p = new AlphaBetaPlayer(Player.PlayerType.WHITE, 1);
    }
	
	@Test
	public void testSetSearchDepth() {
		AlphaBetaPlayer abPlayer = new AlphaBetaPlayer(Player.PlayerType.WHITE);
		abPlayer.setSearchDepth(2);
		assertEquals(abPlayer.getSearchDepth(), 2);

	}

	@Test
	public void testGetSearchDepth() {
		AlphaBetaPlayer abPlayer = new AlphaBetaPlayer(Player.PlayerType.WHITE, 3);
		assertEquals(abPlayer.getSearchDepth(), 3);
	}
	
	@Test
	public void testChooseMoveBestLine() {
		AlphaBetaPlayer abPlayer = new AlphaBetaPlayer(Player.PlayerType.WHITE, 1);
		Board.BoardState[][] testBoardData = DebugFunctions.makeSolidBoardStateArray(Board.BoardState.EMPTY);

		testBoardData[0][0] = Board.BoardState.WHITE;
		testBoardData[0][1] = Board.BoardState.BLACK;
		testBoardData[0][2] = Board.BoardState.BLACK;
		
		testBoardData[3][0] = Board.BoardState.WHITE;
		testBoardData[3][1] = Board.BoardState.BLACK;
		testBoardData[3][2] = Board.BoardState.BLACK;
		testBoardData[3][3] = Board.BoardState.BLACK;
		Board testBoard = new Board(testBoardData, 7);
		testBoard.visualise();
		
		assertEquals(abPlayer.chooseMove(testBoard), new Move(3,4));
	}
}
