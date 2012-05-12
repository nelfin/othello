package iago.features;

import iago.*;
import iago.players.Player;

public class StoneCount extends Feature{
	
	private static final double DEFAULT_WEIGHT = 1;
	
	public StoneCount(double weight)
	{
		super("Stone Count", "Number of player's stones - number of opponent(player)'s stones",weight);
	}
	public StoneCount()
	{
		super("Stone Count", "Number of player's stones - number of opponent(player)'s stones",DEFAULT_WEIGHT);
	}
	/**
	 * A very basic feature that counts the number of stones in white's favour
	 * @return # white - # black
	 */
	public Integer evaluate(Board state, Player.PlayerType player)
	{
		return state.scoreBoard(player);
	}
}
