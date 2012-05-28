package iago.players;

import iago.Board;
import iago.Move;
import iago.history.OpeningBook;
import iago.history.UnexploredException;

public class OpeningBookPlayer extends AbstractPlayer{
	OpeningBook openingBook;

	public OpeningBookPlayer(PlayerType colour) {
		super(colour);
		openingBook = new OpeningBook();
	}

	@Override
	public Move chooseMove(Board board) {
		Move otherPlayersMove = board.getMostRecentlyPlayedMove();
		try {
            return openingBook.getNextOpeningMove(otherPlayersMove);
        } catch (UnexploredException e) {
            return Move.NO_MOVE;
        }
	}

}
