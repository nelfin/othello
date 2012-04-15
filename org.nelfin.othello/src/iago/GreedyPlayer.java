package iago;

import java.util.Set;

public class GreedyPlayer extends AbstractPlayer {
    
    @Override
    public Move chooseMove(Board board) {
        Set<Move> legalMoves = board.validMoves(getColour());
        if (legalMoves.size() == 0) {
            return Move.NO_MOVE;
        } else {
            int maxScore = -1;
            Move bestMove = null;
            for (Move m : legalMoves) {
                int score = board.scoreMove(m, getColour());
                if (score > maxScore) {
                    maxScore = score;
                    bestMove = m;
                }
            }
            assert (null != bestMove);
            return bestMove;
        }
    }
    
    public GreedyPlayer(PlayerType colour) {
        super(colour);
    }
}
