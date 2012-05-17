package iago.players;

import iago.features.FeatureSet;

public abstract class LearningPlayer extends AbstractPlayer {

	public LearningPlayer(PlayerType colour) {
		super(colour);
		// TODO Auto-generated constructor stub
	}
	/**
	 * Allows a learning player to receive feedback based on it's performance.
	 * @param feedback	A double between 0 and 1 with 0 representing the most negative feedback, and 1 representing the most positive feedback.
	 */
	public abstract void receiveFeedback(double feedback);
	/**
	 * Return's the learning player's current feature set (and weightings, of course)
	 * @return The current feature set
	 */
	public abstract FeatureSet getFeatureSet();
	
	/**
	 * Sets the learning player's current feature set. The policy iteration should be done in the receiveFeedback function, this is just so we can clone a player
	 * @param featureSet	The FeatureSet that the player plans to take on
	 */
	public abstract void setFeatureSet(FeatureSet featureSet);

}
