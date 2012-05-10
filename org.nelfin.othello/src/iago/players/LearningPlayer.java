package iago.players;

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

}
