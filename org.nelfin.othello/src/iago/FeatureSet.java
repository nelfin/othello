package iago;

import java.util.ArrayList;

public class FeatureSet {

	private ArrayList<Feature> Features;
	
	public FeatureSet (ArrayList<Feature> Features) {
		this.Features = Features;
	}
	
	public void add (Feature f) {
		Features.add(f);
	}

	public Integer score (Board state){
		Integer boardscore = 0;
		//Evaluate victory condition (this bit probably unnecessary)
		Boolean victory = false;
		if (victory) return Integer.MAX_VALUE;
		//Loop through features and evaluate each one
		for (Feature f: Features)
			boardscore += f.evaluate(state);
		return boardscore;
	}
	
	//A game has just ended, evaluate and adjust features based on Win/Loss
	public void update (FeatureSet opponent, Board finalstate) {
		//Learn how to learn
	}
}
