package iago.features;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;
import iago.Board;
import iago.players.Player;

import org.junit.Test;


public class SidePiecesTest {
	private SidePieces sideCount = new SidePieces(1);

	@Test
	public void testCornerPieces(){
		Board testBoard = new Board("w........w" +
                					"..*......." +
                					"w.....b..." +
                					".........." +
                					"....ww...." +
                					"....bb...." +
                					"b........w" +
                					"....b....." +
                					"........*." +
                					"w.....b..w");
		assertTrue(sideCount.evaluate(testBoard,Player.PlayerType.WHITE) == 6);
	}
	
	
}
