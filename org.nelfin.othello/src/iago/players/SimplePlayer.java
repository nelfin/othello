package iago.players;

import iago.Board;
import iago.Move;

import java.util.Set;

public class SimplePlayer extends AbstractPlayer {
    /* (non-Javadoc)
     * @see iago.Player#chooseMove(iago.Board)
     */
    public Move chooseMove(Board board) {
        Set<Move> legalMoves = board.validMoves(getColour());
        if (legalMoves.size() == 0) {
            return Move.NO_MOVE;
        } else {
            return legalMoves.iterator().next();
        }
    }
    
    public SimplePlayer(PlayerType colour) {
        super(colour);
    }
}
