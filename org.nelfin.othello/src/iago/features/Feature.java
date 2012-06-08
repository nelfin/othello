package iago.features;

import java.io.Serializable;

import iago.Board;
import iago.players.Player;

/**
 * Abstract feature implementation. True features should override evaluate()
 * at a minimum
 *
 */
public abstract class Feature implements Serializable, Cloneable{
	private static final long serialVersionUID = 1L;

	private double weight;

	public final String description;
	public final String name;
	public final Integer bestScore;
	
	//Feature classes should pass static hard-coded constants for first 3.
	public Feature (String name, String description, double weight, Integer bestScore) {
		this.name        = name;
		this.description = description;
		this.weight = weight;
		this.bestScore = bestScore;
	}
	
	public Feature(Feature other) {
	    this.name = other.name;
	    this.description = other.description;
	    this.weight = other.weight;
	    this.bestScore = other.bestScore;
	}
	
	@Override
	 public Object clone() throws CloneNotSupportedException {
		    return super.clone();
	 }
	
	public void setWeight(double weight) {
		this.weight = weight;
		if(this.weight < 0) this.weight = 0;

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
