package iago.features;

import iago.Board;
import iago.players.Player;
import iago.players.Player.PlayerType;

public class CornerPieces extends Feature{
	
	public CornerPieces(double weight) {
		super("CornerPieces", "The number of side pieces occupied by the player", weight);
	}

	
	
	/**
	 * The number of corner pieces for white
	 * @return corner pieces white
	 */
	public Integer evaluate(Board state)
	{
		int cornerScore = 0;
		if(state.get(0, 0)==Board.BoardState.asBoardState(PlayerType.WHITE)) cornerScore++;
		if(state.get(0, Board.BOARD_SIZE)==Board.BoardState.asBoardState(PlayerType.WHITE)) cornerScore++;
		if(state.get(Board.BOARD_SIZE, Board.BOARD_SIZE)==Board.BoardState.asBoardState(PlayerType.WHITE)) cornerScore++;
		if(state.get(Board.BOARD_SIZE, Board.BOARD_SIZE)==Board.BoardState.asBoardState(PlayerType.WHITE)) cornerScore++;

		return cornerScore;
	}
}
