package iago;

public class DebugFunctions {
	public static char[][] makeSolidBoardCharArray(char boardContents)
	{
		char[][] solidBoardData = new char[Board.BOARD_SIZE][Board.BOARD_SIZE];
		for(int x = 0; x < Board.BOARD_SIZE; x++)
		{
			for(int y = 0; y < Board.BOARD_SIZE; y++)
			{
				solidBoardData[x][y] = boardContents;
			}
		}
		return solidBoardData;
	}
	
	public static String charArrayToBoardString(char[][] charArray)
	{
		String boardString = "";
		for(int x = 0; x < Board.BOARD_SIZE; x++)
		{
			for(int y = 0; y < Board.BOARD_SIZE; y++)
			{
				boardString += charArray[y][x];
			}
		}
		return boardString;
	}
}
