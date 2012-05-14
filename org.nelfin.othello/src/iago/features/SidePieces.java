package iago.features;

import iago.Board;
import iago.players.Player;
import iago.players.Player.PlayerType;

public class SidePieces extends Feature{
	
	public SidePieces(double weight) {
		super("SidePieces", "The number of side pieces occupied by the player", weight);
	}

	
	
	/**
	 * The number of legal moves for the next player
	 * @return Legal moves white
	 */
	public Integer evaluate(Board state)
	{
		int sideScore = 0;
		for(int x = 0; x < Board.BOARD_SIZE; x++) if(state.get(x, 0)==Board.BoardState.asBoardState(PlayerType.WHITE)) sideScore++;
		for(int x = 0; x < Board.BOARD_SIZE; x++) if(state.get(x, Board.BOARD_SIZE)==Board.BoardState.asBoardState(PlayerType.WHITE)) sideScore++;
		for(int y = 0; y < Board.BOARD_SIZE; y++) if(state.get(0, y)==Board.BoardState.asBoardState(PlayerType.WHITE)) sideScore++;
		for(int y = 0; y < Board.BOARD_SIZE; y++) if(state.get(Board.BOARD_SIZE, y)==Board.BoardState.asBoardState(PlayerType.WHITE)) sideScore++;

		return sideScore;
	}
}
