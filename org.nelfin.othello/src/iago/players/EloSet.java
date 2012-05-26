package iago.players;

import java.util.ArrayList;
import java.util.Random;

import iago.features.*;

public class EloSet extends FeatureSet implements Comparable<EloSet>{

	private static final long serialVersionUID = -1039441396732532388L;
	private int ELO;
	private static final int K = 64;
	private static final double variance = 0.07;
	private static final int influencers = 5;
	Random r = new Random();
	
	public EloSet(int ELO, String name) {
		super(name);
		this.setELO(ELO);
		//Note: does nothing if player does not already exist
		this.load();
	}
	
	public int updateELO(int otherELO, double actual){
		double expected = 1 / (1 + Math.pow(10,((otherELO-getELO()) / 400)));
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
		Features.add(new SidePieces(r.nextDouble()));
		Features.add(new CornerPieces(r.nextDouble()));
		this.standardiseWeights();
	}

	// Make a new champ by scaling off top ELO champs. NOTE! champs assumed sorted!
	public void makeScaledWeights(ArrayList<EloSet> champs) {
		Features = new ArrayList<Feature>();
		Features.add(new StoneCount());
		Features.add(new Visibility());
		Features.add(new LegalMoves());
		Features.add(new SidePieces(r.nextDouble()));
		Features.add(new CornerPieces(r.nextDouble()));
		for (Feature f : Features) {
			double weighttotal = 0;
			int elototal = 0;
			for (int i = 0; i < influencers && i < champs.size(); i++) {
				EloSet c = champs.get(i);
				for (Feature o : c.Features) {
					if (f.name.equals(o.name)) {
						weighttotal += c.getELO()*o.getWeight();
						elototal += c.getELO();
					}
				}
			}
			System.out.println(weighttotal / elototal);
			f.setWeight(r.nextGaussian()*variance + weighttotal / elototal);
		}
		this.standardiseWeights();
	}

	@Override
	public int compareTo(EloSet other) {
		return this.getELO() - other.getELO();
	}
}
