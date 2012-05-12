package iago.features;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class FeatureSetTest {
	@Test
	public void testNormaliseWeights(){
		FeatureSet fs = new FeatureSet();
		fs.add(new StoneCount(1));
		fs.add(new Visibility(2));
		fs.add(new Visibility(-100));
		fs.normaliseWeights();
		for(Feature f : fs){
			assertTrue(f.getWeight() <= 1);
		}
	}
}
