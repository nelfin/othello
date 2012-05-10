package iago.players;

import iago.Board;
import iago.Move;
import iago.features.Feature;
import iago.features.FeatureSet;

public class MetaPlayer extends LearningPlayer{
	private NegamaxPlayer negamaxPlayer;

	public MetaPlayer(PlayerType colour) {
		super(colour);
		negamaxPlayer = new NegamaxPlayer(colour);
	}

	@Override
	public Move chooseMove(Board board) {
		return negamaxPlayer.chooseMove(board);
	}

	@Override
	public void receiveFeedback(double feedback) {
		
	}

}
