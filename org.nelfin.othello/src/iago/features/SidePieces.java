package iago.features;

import iago.Board;
import iago.players.Player;
import iago.players.Player.PlayerType;

/**
 * The number of side pieces occupied by the player.
 * This is a weaker indicator of stability. Side pieces are more
 * likely to be stable than fringe or interior pieces.
 *
 */
public class SidePieces extends Feature{
	
	public SidePieces(double weight) {
		super("SidePieces", "The number of side pieces occupied by the player", weight, Board.BOARD_SIZE*4);
	}


	/**
	 * The number of legal moves for the next player
	 * @return Legal moves player
	 */
	@Override
	public Integer evaluate(Board state, PlayerType player) {
		int sideScore = 0;
		for(int x = 0; x < Board.BOARD_SIZE; x++) if(state.get(x, 0)==Board.BoardState.asBoardState(player)) sideScore++; 
		for(int y = 1; y < Board.BOARD_SIZE; y++) if(state.get(Board.BOARD_SIZE-1, y)==Board.BoardState.asBoardState(player)) sideScore++;
		for(int x = 0; x < Board.BOARD_SIZE-1; x++) if(state.get(x, Board.BOARD_SIZE-1)==Board.BoardState.asBoardState(player)) sideScore++;
		for(int y = 1; y < Board.BOARD_SIZE-1; y++) if(state.get(0, y)==Board.BoardState.asBoardState(player)) sideScore++;
		return sideScore;
	}
}
