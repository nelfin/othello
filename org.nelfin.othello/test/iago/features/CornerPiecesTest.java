package iago.features;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;
import iago.Board;
import iago.players.Player;

import org.junit.Test;


public class CornerPiecesTest {
	private CornerPieces cornerCount = new CornerPieces(1);

	@Test
	public void testCornerPieces(){
		Board testBoard = new Board("w........w" +
                					"..*......." +
                					".........." +
                					".........." +
                					"....ww...." +
                					"....bb...." +
                					".........." +
                					".........." +
                					"........*." +
                					"b........w");
		assertTrue(cornerCount.evaluate(testBoard,Player.PlayerType.WHITE) == 3);
	}
	
	
}
