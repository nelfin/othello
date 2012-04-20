package iago;

public class DebugFunctions {
	public static Board.BoardState[][] makeSolidBoardStateArray(Board.BoardState state){
		Board.BoardState[][] boardStateArray = new Board.BoardState[Board.BOARD_SIZE][Board.BOARD_SIZE];
		for(int x = 0; x < Board.BOARD_SIZE; x++)
		{
			for(int y = 0; y < Board.BOARD_SIZE; y++)
			{
				boardStateArray[x][y] = state;
			}
		}
		return boardStateArray;
	}
}
