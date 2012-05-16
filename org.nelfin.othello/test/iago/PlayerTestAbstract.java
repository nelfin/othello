package iago;

import iago.players.Player;
import junit.framework.TestCase;

import org.junit.Test;

public abstract class PlayerTestAbstract extends TestCase{
	protected Player greedyPlayer;
	
	@Test
	public void testChooseMoveFlipLine() {
		char[][] testBoardData = DebugFunctions.makeSolidBoardCharArray('.');
		testBoardData[0][0] = 'w';
		testBoardData[0][1] = 'b';
		testBoardData[0][2] = 'b';
		Board testBoard = new Board(DebugFunctions.charArrayToBoardString(testBoardData));

		assertEquals(greedyPlayer.chooseMove(testBoard), new Move(0,3));
	}
	@Test
	public void testOnlyAvailableMove() {
		//AlphaBetaPlayer greedyPlayer = new AlphaBetaPlayer(Player.PlayerType.WHITE, 1);
		char[][] testBoardData = DebugFunctions.makeSolidBoardCharArray('*');
		Move correctMove = new Move(4,2);
		testBoardData[correctMove.x][correctMove.y] = '.';
		testBoardData[correctMove.x-1][correctMove.y] = 'b';
		testBoardData[correctMove.x-2][correctMove.y] = 'w';

		Board testBoard = new Board(DebugFunctions.charArrayToBoardString(testBoardData));
		assertEquals(greedyPlayer.chooseMove(testBoard), correctMove);
	}
	

	


}