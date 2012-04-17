package iago;

import java.util.Set;


public class MinimaxPlayer extends AbstractPlayer {
    
    public static final int DEFAULT_DEPTH = 4;
    private static final int INF = 65535;
    private static final PlayerType MAX_PLAYER = PlayerType.WHITE;
    
    private int searchDepth;
    private Move bestMove;
    private boolean isMaxPlayer;
    
    @Override
    public Move chooseMove(Board board) {
        this.bestMove = null;
        
        minimax(board, getColour(), getSearchDepth());
        
        if (this.bestMove == null) {
            // This shouldn't happen...
            System.out.println("[minimax] We didn't pick a move!");
            board.visualise();
            Set<Move> legalMoves = board.validMoves(getColour());
            for (Move m : legalMoves) {
                System.out.println("[minimax] " + m.toString());
            }
            return Move.NO_MOVE;
        }
        
        return this.bestMove;
    }
    
    private int minimax(Board board, PlayerType player, int depth) {
        if ((depth <= 0) || (board.movesRemaining() == 0)) {
            return board.scoreBoardObjectively();
        }
        
        PlayerType nextPlayer = player.getOpponent();
        Set<Move> successors = board.validMoves(player);
        int alpha;
        Move lBestMove = null;
        
        if (player == MAX_PLAYER) {
            alpha = -INF; 
        } else {
            alpha = INF;
        }
        
        for (Move m : successors) {
            int v = minimax(board.apply(m, player), nextPlayer,
                    depth-1);
            if (player == MAX_PLAYER) {
                if (v > alpha || alpha == -INF) {
                    alpha = v;
                    if (isMaxPlayer) {
                        lBestMove = m;
                    }
                }
            } else {
                if (v < alpha || alpha == INF) {
                    alpha = v;
                    if (!isMaxPlayer) {
                        lBestMove= m;
                    }
                }
            }
        }
        
        //System.err.println("[minimax] picking best move in depth " + depth);
        this.bestMove = lBestMove;
        return alpha;
    }

    public MinimaxPlayer(PlayerType colour) {
        this(colour, DEFAULT_DEPTH);
    }
    
    public MinimaxPlayer(PlayerType colour, int depth) {
        super(colour);
        this.searchDepth = depth;
        this.isMaxPlayer = (colour == MAX_PLAYER); 
    }
    
    public void setSearchDepth(int searchDepth) {
        this.searchDepth = searchDepth;
    }
    
    public int getSearchDepth() {
        return searchDepth;
    }
    
}
