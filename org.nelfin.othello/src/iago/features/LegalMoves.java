package iago.features;

import java.util.HashSet;

import iago.Board;
import iago.players.*;

public class LegalMoves extends Feature{
	
	private HashSet<Integer[]> blockedPoints = new HashSet<Integer[]>();
	private Integer[][] visibilityMap = new Integer[Board.BOARD_SIZE][Board.BOARD_SIZE];
	private boolean visibilityMapInitialised = false;
	public LegalMoves(double weight)
	{
		super("LegalMoves", "The number of legal moves for white", weight);
	}
	
	
	
	/**
	 * The number of legal moves for the next player
	 * @return Legal moves white
	 */
	public Integer evaluate(Board state, Player.PlayerType player)
	{
		return state.validMoves(player).size();
	}
}
