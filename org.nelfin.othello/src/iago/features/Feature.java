package iago.features;

import iago.Board;
import iago.players.Player;


public abstract class Feature {

	private double weight;

	public final String description;
	public final String name;
	
	//Feature classes should pass static hard-coded constants for first 3.
	public Feature (String name, String description, double weight) {
		this.name        = name;
		this.description = description;
		this.weight = weight;
	}
	
	public void setWeight(double weight) {
		this.weight = weight;
	}

	//Many features may wish to override this and scale their weights rather than
	//direct over-write
	//Maybe should be aware of win/loss state...
	public void updateWeight(double weight) {
		this.setWeight(weight);
	}
	//For use in the feature set
	public double getWeight() {
		return weight;
	}
	
	public abstract Integer evaluate(Board state, Player.PlayerType player);

}
