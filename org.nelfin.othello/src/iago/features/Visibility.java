package iago.features;

import java.util.HashSet;

import iago.Board;

public class Visibility extends Feature{
	
	private HashSet<Integer[]> blockedPoints = new HashSet<Integer[]>();
	private Integer[][] visibilityMap = new Integer[Board.BOARD_SIZE][Board.BOARD_SIZE];
	private boolean visibilityMapInitialised = false;
	public Visibility(double weight)
	{
		super("Visibility", "The visibility of white's squares - the visibility of black's squares", weight);
	}
	
	
	/**
	 * Recalculates the map of all the position's visibilities
	 * @param state		The board to evaluate the positions for
	 */
	private void evaluateVisibilityMap(Board state)
	{
		visibilityMapInitialised = true;

		for(int x=0;x<Board.BOARD_SIZE;x++){
			for(int y=0;y<Board.BOARD_SIZE;y++){
				int visibility = 0;
				for(int dx=-1;dx<=1;dx++)
				{
					for(int dy=-1;dy<=1;dy++) //checks in all 8 directions
					{
						if(dx==0 && dy==0){ //don't bother with the center
							continue;
						}else{
							visibility += checkDirection(state,x,y,dx,dy);
						}
					}
					//We have this cell's visibility
					visibilityMap[x][y] = visibility;
				}

			}
		}
	}
	/**
	 * Recursively find the visibility from each position
	 * @param state		The board state (used for checking blocked positions only)
	 * @param x			The current x coordinate
	 * @param y			The current y coordinate
	 * @param dx		The x direction we are checking in
	 * @param dy		The y direction we are checking in 
	 */
	private int checkDirection(Board state, int x, int y, int dx, int dy)
	{
		boolean outOfBounds = (x < 0 || y < 0 || x >= Board.BOARD_SIZE || y >= Board.BOARD_SIZE);
		if(!outOfBounds)
		{
			if(!(state.get(x, y)==Board.BoardState.BLOCKED))
			{
				return checkDirection(state,x+dx,y+dy,dx,dy) + 1;
			}
		}
		return 0;
	}
	/**
	 * Visibility is the number of squares in any direction until a side or a block is reached
	 * @return Visibility white - Visibility black
	 */
	public Integer evaluate(Board state)
	{
		HashSet<Integer[]> thisBoardBlockedPoints = new HashSet<Integer[]>();
		for(int x=0;x<Board.BOARD_SIZE;x++){
			for(int y=0;y<Board.BOARD_SIZE;y++){
				if(state.get(x, y)==Board.BoardState.BLOCKED){
					Integer[] point = {x,y};
					thisBoardBlockedPoints.add(point);
				}
			}
		}
		if(!thisBoardBlockedPoints.equals(blockedPoints) || !visibilityMapInitialised){
			evaluateVisibilityMap(state); //This is pretty time consuming so we don't want to recalculate it every time
		}
		//Calculate every white square's visibility and every black square's visibility, find difference
		int whiteVisibilityAdvantage = 0;
		for(int x=0;x<Board.BOARD_SIZE;x++){
			for(int y=0;y<Board.BOARD_SIZE;y++){
				
				if(state.get(x, y)==Board.BoardState.WHITE){
					whiteVisibilityAdvantage += visibilityMap[x][y];
				}else if(state.get(x,y)==Board.BoardState.BLACK){
					whiteVisibilityAdvantage -= visibilityMap[x][y];
				}
			}
		}
		return whiteVisibilityAdvantage;
	}
}
