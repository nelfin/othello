package iago.features;

import static org.junit.Assert.assertTrue;
import iago.Board;
import iago.features.Visibility;
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
		assertTrue(visibility.evaluate(smallBoard) == 6);
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
		assertTrue(visibility.evaluate(emptyBoard) == 0);
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
		assertTrue(visibility.evaluate(symmetryBoard) == 0);
	}
}
