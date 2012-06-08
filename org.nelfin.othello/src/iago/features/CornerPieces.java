package iago.features;

import iago.Board;
import iago.players.Player;
import iago.players.Player.PlayerType;

/**
 * The number of corner pieces occupied by the player.
 * All corner pieces are automatically stable. They cannot be captured by an
 * opponent and thus are inherently valuable.
 *
 */
public class CornerPieces extends Feature{
	
	public CornerPieces(double weight) {
		super("CornerPieces", "The number of corner pieces occupied by the player", weight, 4);
	}

	
	
	/**
	 * The number of corner pieces for player
	 * @return corner pieces player
	 */
	@Override
	public Integer evaluate(Board state, PlayerType player) {
		int cornerScore = 0;
		if(state.get(0, 0)==Board.BoardState.asBoardState(player)) cornerScore++;
		if(state.get(0, Board.BOARD_SIZE-1)==Board.BoardState.asBoardState(player)) cornerScore++;
		if(state.get(Board.BOARD_SIZE-1, 0)==Board.BoardState.asBoardState(player)) cornerScore++;
		if(state.get(Board.BOARD_SIZE-1, Board.BOARD_SIZE-1)==Board.BoardState.asBoardState(player)) cornerScore++;

		return cornerScore;
	}
}
