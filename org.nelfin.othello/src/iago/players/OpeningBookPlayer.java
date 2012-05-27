package iago.players;

import iago.Board;
import iago.Move;
import iago.history.OpeningBook;

public class OpeningBookPlayer extends AbstractPlayer{
	OpeningBook openingBook;

	public OpeningBookPlayer(PlayerType colour) {
		super(colour);
		openingBook = new OpeningBook();
	}

	@Override
	public Move chooseMove(Board board) {
		Move otherPlayersMove = board.getMostRecentlyPlayedMove();
		return openingBook.getNextOpeningMove(otherPlayersMove);
		
	}

}
