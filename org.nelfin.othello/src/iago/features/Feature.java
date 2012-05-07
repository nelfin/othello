package iago.features;

import iago.Board;


public abstract class Feature {

	public String name;
	
	public String description;
	private double weight; 
	
	public Feature (String name, String description, double weight) {
		this.name        = name;
		this.description = description;
		this.weight      = weight;
	}
	
	//Many features may wish to override this and scale their weights rather than
	//direct over-write
	public void updateWeight(double weight) {
		this.weight = weight;
	}
	//For use in the feature set
	public double getWeight() {
		return weight;
	}
	
	public abstract Integer evaluate(Board state);

}
