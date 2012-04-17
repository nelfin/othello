package iago;

import java.util.Set;


public class MinimaxPlayer extends AbstractPlayer {
    
    public static final int DEFAULT_DEPTH = 5;
    private static final int INF = 65535;
    private static final PlayerType MAX_PLAYER = PlayerType.WHITE;
    
    private int searchDepth;
    
    @Override
    public Move chooseMove(Board board) {
        Set<Move> legalMoves = board.validMoves(getColour());
        if (legalMoves.size() == 0) {
            return Move.NO_MOVE;
        }
        
        PlayerType player = getColour();
        PlayerType opponent = player.getOpponent();
        Move bestMove = null;
        int bestScore;
        
        if (player == MAX_PLAYER) {
            bestScore = -(INF+1);
        } else {
            bestScore = INF+1;
        }
        
        for (Move m : legalMoves) {
            Board move = board.apply(m, player);
            // deliberately losing... ???
            int score = minimax(move, opponent, getSearchDepth()-1);
            // FIXED wasn't picking a move under a guaranteed loss
            if (player == MAX_PLAYER) {
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = m;
                }
            } else {
                if (score < bestScore) {
                    bestScore = score;
                    bestMove = m;
                }
            }
        }
        
        assert (null != bestMove);
        return bestMove;
    }
    
    private int minimax(Board board, PlayerType player, int depth) {
        if ((depth <= 0) || (board.movesRemaining() == 0)) {
            return board.scoreBoardObjectively();
        }
        
        PlayerType nextPlayer = player.getOpponent();
        Set<Move> successors = board.validMoves(player);
        int alpha;
        
        if (player == MAX_PLAYER) {
            alpha = INF; 
        } else {
            alpha = -INF;
        }
        
        for (Move m : successors) {
            int v = minimax(board.apply(m, player), nextPlayer,
                    depth-1);
            if (player == MAX_PLAYER) {
                alpha = Math.max(alpha, v);
            } else {
                alpha = Math.min(alpha, v);
            }
        }
        
        return alpha;
    }

    public MinimaxPlayer(PlayerType colour) {
        this(colour, DEFAULT_DEPTH);
    }
    
    public MinimaxPlayer(PlayerType colour, int depth) {
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
