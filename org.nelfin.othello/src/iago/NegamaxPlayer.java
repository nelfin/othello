package iago;

import java.util.Set;


public class NegamaxPlayer extends AbstractPlayer {
    
    public static final int DEFAULT_DEPTH = 2;
    
    private int searchDepth;
    
    @Override
    public Move chooseMove(Board board) {
        Set<Move> legalMoves = board.validMoves(getColour());
        if (legalMoves.size() == 0) {
            return Move.NO_MOVE;
        }
        
        // Note: top-level should always be called with getColour()
        Move bestMove = negamax(board, getColour(), getSearchDepth(),
                Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
        
        assert (null != bestMove);
        return bestMove;
    }
    
    private Move negamax(Board board, PlayerType player, int depth,
            int alpha, int beta, int colour) {
        
        PlayerType nextPlayer = player.getOpponent();
        Set<Move> successors = board.validMoves(player);
        Move alphaMove = Move.NO_MOVE;
        
        if (depth == 0) {
            // XXX should this be getColour() or player?
            return new Move (0, 0, colour * board.scoreBoard(getColour()));
        }
        
        // Current player must pass, play a no-op
        if (successors.size() == 0) {
            Move junk = negamax(board, nextPlayer, depth-1, -beta, -alpha, -colour);
            junk.invert();
            return junk;
        }
        // Situation as usual
        for (Move m : successors) {
            Move junk = negamax(board.apply(m, player), nextPlayer,
                    depth-1, -beta, -alpha, -colour);
            junk.invert();
            int v = junk.score;
            // using a fail-soft cut-off
            if (v >= beta) {
                m.score = v;
                return m;
            }
            if (v >= alpha) {
                m.score = v;
                alphaMove = m;
                alpha = v;
            }
        }
        return alphaMove;
    }

    public NegamaxPlayer(PlayerType colour) {
        this(colour, DEFAULT_DEPTH);
    }
    
    public NegamaxPlayer(PlayerType colour, int depth) {
        super(colour);
        this.searchDepth = depth;
    }
    
    public void setSearchDepth(int searchDepth) {
        this.searchDepth = searchDepth;
    }
    
    public int getSearchDepth() {
        return searchDepth;
    }
    
}
