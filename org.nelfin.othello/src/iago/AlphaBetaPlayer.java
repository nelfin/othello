package iago;

import java.util.Set;


public class AlphaBetaPlayer extends AbstractPlayer {
    
    public static final int DEFAULT_DEPTH = 5;
    
    private int searchDepth;
    
    @Override
    public Move chooseMove(Board board) {
        Set<Move> legalMoves = board.validMoves(getColour());
        if (legalMoves.size() == 0) {
            return Move.NO_MOVE;
        }
        PlayerType opponent = getColour().getOpponent();
        int bestScore = Integer.MIN_VALUE;
        Move bestMove = null;
        for (Move m : legalMoves) {
            int score = minimax(board, opponent, Integer.MIN_VALUE,
                    Integer.MAX_VALUE, getSearchDepth()-1);
            if (score > bestScore) {
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
            return board.scoreBoard(player);
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
