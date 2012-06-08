package iago.features;

import java.util.ArrayList;
import java.util.Random;

/**
 * A combination of features along with Elo ranking functions.
 * Used as a helper class inside of EloArena
 *
 */
public class EloSet extends FeatureSet{

	private static final long serialVersionUID = -1039441396732532388L;
	private int ELO;
	private static final int K = 64;
	private static final double variance = 1;
	Random r = new Random();
	
	public EloSet(int ELO, String name) {
		super(name);
		this.setELO(ELO);
		//Note: does nothing if player does not already exist
		this.load();
	}
	
	public int updateELO(int otherELO, double actual){
		double expected = 1 / (1 + Math.pow(10,((otherELO-getELO()) / 40)));
		System.out.println(expected);
		double diff = (K*(actual-expected)); 
		ELO += diff;
		return (int) diff;
	}

	public void setELO(int ELO) {
		this.ELO = ELO;
	}

	public int getELO() {
		return ELO;
	}
	
	public void makeRandomWeights() {
		Features = new ArrayList<Feature>();
		Features.add(new StoneCount(r.nextDouble()));
		Features.add(new Visibility(r.nextDouble()));
		Features.add(new LegalMoves(r.nextDouble()));
		this.standardiseWeights();
	}

	public void makeScaledWeights(ArrayList<EloSet> champs) {
		// TODO Auto-generated method stub
		Features = new ArrayList<Feature>();
		Features.add(new StoneCount());
		Features.add(new Visibility());
		Features.add(new LegalMoves());
		for (Feature f : Features) {
			double weighttotal = 0;
			int elototal = 0;
			for (EloSet c : champs) for (Feature o : c.Features) {
				if (f.name.equals(o.name)) {
					weighttotal += c.getELO()*o.getWeight();
					elototal += c.getELO();
				}
			}
			System.out.println(weighttotal / elototal);
			f.setWeight(r.nextGaussian()*variance + weighttotal / elototal);
		}
		this.standardiseWeights();
	}
}
