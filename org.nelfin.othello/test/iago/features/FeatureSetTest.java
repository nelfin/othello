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
		fs.standardiseWeights();
		for(Feature f : fs){
			assertTrue(f.getWeight() <= 1);
			assertTrue(f.getWeight() >= -1);
		}
	}
	
	@Test
	public void testFileSaveLoad(){
		FeatureSet fs = new FeatureSet("testface");
		fs.add(new StoneCount(1));
		fs.add(new Visibility(2));
		fs.add(new Visibility(-100));
		fs.saveToFile();
		FeatureSet fsl = new FeatureSet("testface");
		fsl.loadFromFile();
		assertEquals(fs.size(), fsl.size());
		for (int i=0; i<fsl.size(); i++) {
			assertEquals(fs.get(i).getWeight(), fsl.get(i).getWeight(), 0.5);
		}
	}
	
	@Test
	public void testSeriSaveLoad(){
		FeatureSet fs = new FeatureSet("testface");
		fs.add(new StoneCount(1));
		fs.add(new Visibility(2));
		fs.add(new Visibility(-100));
		fs.save();
		FeatureSet fsl = new FeatureSet("testface");
		fsl.load();
		assertEquals(fs.size(), fsl.size());
		for (int i=0; i<fsl.size(); i++) {
			assertEquals(fs.get(i).getWeight(), fsl.get(i).getWeight(), 0.5);
		}
	}
}
