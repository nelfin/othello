package iago.features;

import iago.*;

public class StoneCount extends Feature{
	
	public StoneCount(double weight)
	{
		super("Stone Count", "Number of white's stones - number of black's stones", weight);
	}
	/**
	 * A very basic feature that counts the number of stones in white's favour
	 * @return # white - # black
	 */
	public Integer evaluate(Board state)
	{
		Integer stoneDifference = 0;
		for(int x=0;x<Board.BOARD_SIZE;x++)
		{
			for(int y=0;y<Board.BOARD_SIZE;y++)
			{
				if(state.get(x, y) == Board.BoardState.WHITE)
				{
					stoneDifference++;
				}else if(state.get(x, y) == Board.BoardState.BLACK)
				{
					stoneDifference--;
				}
			}
		}
		return stoneDifference;
	}
}
