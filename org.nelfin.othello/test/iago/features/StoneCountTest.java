package iago.features;
import static org.junit.Assert.assertTrue;
import iago.Board;
import org.junit.Test;


public class StoneCountTest {
	private StoneCount stoneCount = new StoneCount(1);
	@Test
	public void smallBoardTest(){
		Board smallBoard = new Board("w........w" +
	                                 "..*......." +
	                                 ".........." +
	                                 ".........." +
	                                 "....ww...." +
	                                 "....bb...." +
	                                 ".........." +
	                                 ".........." +
	                                 "........*." +
	                                 "w........w");
		assertTrue(stoneCount.evaluate(smallBoard) == 4);
	}
	
	@Test
	public void largeBoardTest(){
		Board smallBoard = new Board("w..***...w" +
                					 "..*.w....." +
                					 "b..*...w.." +
                					 ".w..w...*." +
                					 "....ww..b." +
                					 ".w..bb..b." +
                					 "...*..w..." +
                					 "b..w..b..." +
                					 ".*...w...*" +
                					 "w...b..*.w");
		assertTrue(stoneCount.evaluate(smallBoard) == 6);
	}
	
	@Test
	public void testWeightChanging(){
		StoneCount weightChangingStoneCount = new StoneCount(0.2);
		assertTrue(weightChangingStoneCount.getWeight() == 0.2);
		weightChangingStoneCount.updateWeight(0.8);
		assertTrue(weightChangingStoneCount.getWeight() == 0.8);

	}
	
}
