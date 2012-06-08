package iago.features;

import iago.*;
import iago.players.Player;

/**
 * Difference in player stone counts.
 * A poor indicator mid-game, but this determines whether or not a player
 * wins once the game is completed.
 *
 */
public class StoneCount extends Feature{
	
	private static final double DEFAULT_WEIGHT = 1;
	private static final Integer BEST_SCORE = Board.BOARD_SIZE * Board.BOARD_SIZE;
	public StoneCount(double weight)
	{
		super("Stone Count", "Number of player's stones - number of opponent(player)'s stones",weight, BEST_SCORE);
	}
	public StoneCount()
	{
		super("Stone Count", "Number of player's stones - number of opponent(player)'s stones",DEFAULT_WEIGHT, BEST_SCORE);
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
