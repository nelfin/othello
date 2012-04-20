package iago;

import java.util.Set;


public class NegamaxPlayer extends AbstractPlayer {
    
    public static final int DEFAULT_DEPTH = 6;
    private static final int INF = 65535;
    
    private int searchDepth;
    private Move bestMove;
    
    @Override
    public Move chooseMove(Board board) {
        this.bestMove = null;
        
        negamax(board, getColour(), 1, -INF, INF, getSearchDepth());
        
        if (this.bestMove == null) {
            return pickGreedyMove(board);
        }
        
        return this.bestMove;
    }
    
    private int negamax(Board board, PlayerType player,
            int colour, int alpha, int beta, int depth) {
        if ((depth <= 0) || board.isVictory()) {
            return colour * board.scoreBoard(player);
        }
        
        PlayerType nextPlayer = player.getOpponent();
        Set<Move> successors = board.validMoves(player);
        Move lBestMove = null;
        
        if (successors.size() == 0) {
            return -negamax(board, nextPlayer, -colour, -beta, -alpha, depth-1);
        }
        
        for (Move m : successors) {
            int v = -negamax(board.apply(m, player, false), nextPlayer,
                    -colour, -beta, -alpha, depth-1);
            if (v >= beta) {
                if (player == getColour()) {
                    this.bestMove = m;
                }
                return v;
            }
            if (v > alpha) {
                alpha = v;
                if (player == getColour()) {
                    lBestMove = m;
                }
            }
        }
        
        if (lBestMove != null) {
            this.bestMove = lBestMove;
        }
        return alpha;
    }
    
    private Move pickGreedyMove(Board board) {
        Set<Move> legalMoves = board.validMoves(getColour());
        Move bestMove = Move.NO_MOVE;
        int maxScore = -INF;
        
        for (Move m : legalMoves) {
            int score = board.scoreMove(m, getColour());
            if (score > maxScore) {
                maxScore = score;
                bestMove = m;
            }
        }
        
        return bestMove;
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
