package iago.features;

import iago.Board;
import iago.Board.BoardState;
import iago.players.Player;
import iago.players.Player.PlayerType;

/**
 * The number of pieces occupied by the player which are adjacent to blocked squares.
 * This is a strong heuristic for stable pieces.
 *
 */
public class BlockedAdjacent extends Feature{
	
	public BlockedAdjacent(double weight) {
		super("BlockedAdjacent", "The number of pieces occupied by the player which are adjacent to blocked squares", weight, 8*4 + 3*40);
	}

	
	
	/**
	 * The number of blocked squares adjacent to player pieces (incl. edges and corners)
	 * @return blocked adjacent player
	 */
	@Override
	public Integer evaluate(Board state, PlayerType player) {
		int stability = 0;
		for (int x = 0; x < Board.BOARD_SIZE; x++)
            for (int y = 0; y < Board.BOARD_SIZE; y++)
            	if (state.get(x, y) == Board.BoardState.asBoardState(player))
            		for (int dx = -1; dx < 2; dx++)
                        for (int dy = -1; dy < 2; dy++)
                        	if (dy != dx && !state.validLocation(x+dx, y+dy)) stability++;

		return stability;
	}
}
