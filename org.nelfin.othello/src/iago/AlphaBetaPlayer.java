package iago;

import java.util.Set;


public class AlphaBetaPlayer extends AbstractPlayer {
    
    public static final int DEFAULT_DEPTH = 2;
    
    private int searchDepth;
    
    @Override
    public Move chooseMove(Board board) {
        Set<Move> legalMoves = board.validMoves(getColour());
        if (legalMoves.size() == 0) {
            return Move.NO_MOVE;
        }
        
        PlayerType player = getColour();
        PlayerType opponent = player.getOpponent();
        int bestScore = Integer.MIN_VALUE;
        Move bestMove = null;
        
        for (Move m : legalMoves) {
            Board move = board.apply(m, player);
            // XXX this was returning the wrong valued score, so we were 
            // deliberately losing... ???
            // FIXME this needs parity flipping for odd searchDepth
            int score = minimax(move, opponent,
                    Integer.MIN_VALUE, Integer.MAX_VALUE, getSearchDepth()-1);
            // FIXED wasn't picking a move under a guaranteed loss
            if ((score > bestScore) || (bestScore == Integer.MIN_VALUE)) {
                bestScore = score;
                bestMove = m;
            }
        }
        
        assert (null != bestMove);
        return bestMove;
    }
    
    private int minimax(Board board, PlayerType player, int alpha, int beta,
            int depth) {
        if (depth <= 0) {
            // TODO remaining moves...
            return board.scoreBoardObjectively();
        }
        
        PlayerType nextPlayer = player.getOpponent();
        Set<Move> successors = board.validMoves(player);
        
        if (player == getColour()) {
            for (Move m : successors) {
                int v = minimax(board.apply(m, player), nextPlayer,
                                alpha, beta, depth-1);
                alpha = Math.max(alpha, v);
                if (beta <= alpha) {
                    break;
                }
            }
        } else {
            for (Move m : successors) {
                int v = minimax(board.apply(m, player), nextPlayer,
                                alpha, beta, depth-1);
                beta = Math.min(beta, v);
                if (beta <= alpha) {
                    break;
                }
            }
        }
        
        if (player == getColour()) {
            return alpha;
        } else {
            return beta;
        }
    }

    public AlphaBetaPlayer(PlayerType colour) {
        this(colour, DEFAULT_DEPTH);
    }
    
    public AlphaBetaPlayer(PlayerType colour, int depth) {
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
