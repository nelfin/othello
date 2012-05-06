package iago;

public abstract class Feature {

	private double weight;
	
	public final double defaultWeight;
	public final String description;
	public final String name;
	
	//Feature classes should pass static hard-coded constants for first 3.
	public Feature (String name, String description, double defaultWeight, double weight) {
		this.name        = name;
		this.description = description;
		this.weight = weight;
		this.defaultWeight = defaultWeight;
	}
	
	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getWeight() {
		return weight;
	}

	//Many features may wish to override this and scale their weights rather than
	//direct over-write
	//Maybe should be aware of win/loss state...
	public void updateWeight(double weight) {
		this.setWeight(weight);
	}
	
	public abstract Integer evaluate(Board state);

}
