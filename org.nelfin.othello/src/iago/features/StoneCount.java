package iago.features;

import iago.*;
import iago.players.Player;

public class StoneCount extends Feature{
	
	public StoneCount(double weight)
	{
		super("Stone Count", "Number of player's stones - number of opponent(player)'s stones", weight);
	}
	/**
	 * A very basic feature that counts the number of stones in white's favour
	 * @return # white - # black
	 */
	public Integer evaluate(Board state, Player.PlayerType player)
	{
		Integer stoneDifference = 0;
		for(int x=0;x<Board.BOARD_SIZE;x++)
		{
			for(int y=0;y<Board.BOARD_SIZE;y++)
			{
				if(Board.BoardState.asBoardState(player) == state.get(x, y))
				{
					stoneDifference++;
				}else if(Board.BoardState.asBoardState(player.getOpponent()) == state.get(x, y))
				{
					stoneDifference--;
				}
			}
		}
		return stoneDifference;
	}
}
