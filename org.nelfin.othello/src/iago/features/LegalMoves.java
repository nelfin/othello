package iago.features;

import iago.Board;
import iago.players.Player;

/**
 * The number of legal moves a player has
 * This is the essential indicator of mobility, a valuable early- and
 * mid-game property
 *
 */
public class LegalMoves extends Feature{
	
	private static final double DEFAULT_WEIGHT = 1;
	private static final Integer BEST_SCORE = Board.BOARD_SIZE * Board.BOARD_SIZE - 4;
	public LegalMoves(double weight)
	{
		super("LegalMoves", "The number of legal moves for white",weight, BEST_SCORE);
	}
	
	public LegalMoves()
	{
		super("LegalMoves", "The number of legal moves for white",DEFAULT_WEIGHT, BEST_SCORE);
	}
	
	
	/**
	 * The number of legal moves for the next player
	 * @param state		The board
	 * @param player	The player
	 * @return Legal moves white
	 */
	public Integer evaluate(Board state, Player.PlayerType player)
	{
		return state.validMoves(player).size();
	}
}
