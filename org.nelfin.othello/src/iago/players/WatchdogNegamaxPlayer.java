package iago.players;

import iago.Board;
import iago.Move;
import iago.features.*;

import java.util.Set;


public class WatchdogNegamaxPlayer extends AbstractPlayer {
    
    public static final int INITIAL_DEPTH = 4;
    public static final int DEEPENING = 2;
    public static final int DEFAULT_DEPTH = 6;
    public static final int DEFAULT_SORT_DEPTH = 3;
    private static final int INF = 65535;
    private static final int COMFORT_ZONE = 500;
    
    private int searchDepth;
    private int sortDepth;
    private Move bestMove;
    private FeatureSet features = new FeatureSet("negamax");
    
    @Override
    public Move chooseMove(final Board board, long deadline) {
        Move currentBestMove = Move.NO_MOVE;
        this.setSearchDepth(INITIAL_DEPTH);
        long last = System.currentTimeMillis();
        while (true) {
            long now = System.currentTimeMillis();
            deadline -= (now - last);
            
            Thread t = (new Thread() {
                public void run() {
                    Move m = chooseMove(board);
                    yieldMove(m);
                }
                
                public void interrupt() {
                    setSearchDepth(0);
                }
            });
            
            t.start();
            try {
                if (deadline <= COMFORT_ZONE) {
                    t.interrupt();
                    break;
                }
                System.out.println("[id-negamax] waiting for "  + (deadline - COMFORT_ZONE) + "ms");
                t.join(deadline - COMFORT_ZONE);
                if (t.isAlive()) {
                    // we haven't died yet, oh noes!
                    t.interrupt();
                    break;
                }
                currentBestMove = grabMove();
            } catch (InterruptedException e) {
                // Watchdog went off
                System.out.println("Watchdog bark bark! Failed search depth at " + searchDepth);
                break;
            }
            this.increaseSearchDepth(DEEPENING);
            last = now;
        }
        return currentBestMove;
    }
    
    private Move grabMove() {
        return this.bestMove;
    }
    
    protected void yieldMove(Move m) {
        this.bestMove = m;
    }
    
    @Override
    public Move chooseMove(Board board) {
        this.bestMove = null;
        
        negamax(board, getColour(), 1, -INF, INF, searchDepth, 0);
        
        if (this.bestMove == null) {
            return pickGreedyMove(board);
        }
        
        return this.bestMove;
    }
    
    private double negamax(Board board, PlayerType player,
            int colour, double alpha, double beta, int depth, int plies) {
        if ((depth <= 0) || board.isVictory()) {
            return colour * features.score(board, player);
        }
        
        PlayerType nextPlayer = player.getOpponent();
        Set<Move> successors;
        if (plies < sortDepth) {
            successors = board.validMovesSorted(player);
        } else {
            successors = board.validMoves(player);
        }
        Move lBestMove = null;
        
        if (successors.size() == 0) {
            return -negamax(board, nextPlayer, -colour, -beta, -alpha, depth-1, plies+1);
        }
        
        for (Move m : successors) {
            double v = -negamax(board.apply(m, player, false), nextPlayer,
                    -colour, -beta, -alpha, depth-1, plies+1);
            if (v >= beta) {
                if (plies == 0 && player == getColour()) {
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
        
        if (plies == 0 && lBestMove != null) {
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
    
    //TODO: add a constructor that uses a minimal Feature Set if no feature set is specified
    
    public WatchdogNegamaxPlayer(PlayerType colour) {
        this(colour, DEFAULT_DEPTH, DEFAULT_SORT_DEPTH);
    }
    
    public WatchdogNegamaxPlayer(PlayerType colour, int depth) {
        this(colour, depth, DEFAULT_SORT_DEPTH);
    }
    
    public WatchdogNegamaxPlayer(PlayerType colour, int depth, int sortDepth) {
        super(colour);
        this.searchDepth = depth; 
        this.setSortDepth(sortDepth);
        //Choose the features here
        features.add(new StoneCount(0.4));
        features.add(new LegalMoves(0.6));
    }
    
    private void setSearchDepth(int searchDepth) {
        this.searchDepth = searchDepth;
    }
    
    private void increaseSearchDepth(int delta) {
        this.searchDepth += delta;
    }
    
    public int getSearchDepth() {
        return searchDepth;
    }
    
    private void setSortDepth(int sortDepth) {
        this.sortDepth = sortDepth;
    }
    
    public int getSortDepth() {
        return sortDepth;
    }

    public void setFeatureSet(FeatureSet features) {
    	this.features = features;
    }
    
    public FeatureSet getFeatureSet() {
    	return features;
    }
    
}
