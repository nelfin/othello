package iago.features;

import iago.Board;
import iago.players.Player;

public class LegalMoves extends Feature{
	
	public LegalMoves(double weight)
	{
		super("LegalMoves", "The number of legal moves for white", weight);
	}
	
	
	
	/**
	 * The number of legal moves for the next player
	 * @return Legal moves white
	 */
	public Integer evaluate(Board state)
	{
		return state.validMoves(Player.PlayerType.WHITE).size();
	}
}
