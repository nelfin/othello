package iago.features;

import static org.junit.Assert.assertTrue;
import iago.Board;
import iago.features.Visibility;
import iago.players.Player;

import org.junit.Test;

public class VisibilityTest {
	private Visibility visibility = new Visibility(1);
	@Test
	public void smallBoardTest(){
		Board smallBoard = new Board("w..*******" +
	                                 "...*******" +
	                                 "...*******" +
	                                 "**********" +
	                                 "**********" +
	                                 "**********" +
	                                 "**********" +
	                                 "**********" +
	                                 "**********" +
	                                 "**********");
		assertTrue(visibility.evaluate(smallBoard,Player.PlayerType.WHITE) == 6);
	}
	@Test
	public void emptyBoardTest(){
		Board emptyBoard = new Board(".........." +
	                                 ".........." +
	                                 ".........." +
	                                 ".........." +
	                                 ".........." +
	                                 ".........." +
	                                 ".........." +
	                                 ".........." +
	                                 ".........." +
	                                 "..........");
		assertTrue(visibility.evaluate(emptyBoard,Player.PlayerType.WHITE) == 0);
	}
	
	@Test
	public void symmetryBoardTest(){
		Board symmetryBoard = new Board("w........b" +
				 						".........." +
				 						".........." +
				 						".........." +
				 						"....**...." +
				 						"....**...." +
				 						".........." +
				 						".........." +
				 						".........." +
									    "w........b");
		assertTrue(visibility.evaluate(symmetryBoard,Player.PlayerType.WHITE) == 0);
	}
}
