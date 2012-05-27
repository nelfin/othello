package iago.players;

import iago.Board;
import iago.Move;
import iago.features.*;
import iago.players.Player.PlayerType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class NegamaxPlayer extends AbstractPlayer {
    
    public static final int DEFAULT_DEPTH = 6;
    public static final int DEFAULT_SORT_DEPTH = 3;
    private static final int INF = 65535;
    
    private int searchDepth;
    private int sortDepth;
    private Move bestMove;
    boolean usetable = false;
    private FeatureSet features = new FeatureSet("negamax");
    
    public long overhead = 0;
    
    public HashMap<String, Double> stateTable = new HashMap<String, Double>();
    
    @Override
    public Move chooseMove(Board board) {
        this.bestMove = null;
        
        Double movescore = negamax(board, getColour(), 1, -INF, INF, getSearchDepth(), 0);
        
        if (this.bestMove == null) {
            return pickGreedyMove(board);
        }
        //else {
        //	stateTable.put(board.toString(), movescore);
        //}
        
        return this.bestMove;
    }
    
    private double negamax(Board board, PlayerType player,
            int colour, double alpha, double beta, int depth, int plies) {
    	
        double v = 0;
    	
        long time = System.currentTimeMillis();
    	if (usetable && stateTable.containsKey(board.toString())) {
    		//System.out.println("HIT!");
    		v = stateTable.get(board.toString());
			overhead += System.currentTimeMillis()-time;
			return v;
    	}
    	overhead += System.currentTimeMillis()-time;
    	
        if ((depth <= 0) || board.isVictory()) {
            v = colour * features.score(board, player);
            if (usetable) stateTable.put(board.toString(), v);
            return v;
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
            v =  -negamax(board, nextPlayer, -colour, -beta, -alpha, depth-1, plies+1);
            if (usetable) stateTable.put(board.toString(), v);
            return v;
        }
        
        for (Move m : successors) {
            v = -negamax(board.apply(m, player, false), nextPlayer,
                    -colour, -beta, -alpha, depth-1, plies+1);
            if (v >= beta) {
                if (plies == 0 && player == getColour()) {
                    this.bestMove = m;
                }
                if (usetable) stateTable.put(board.toString(), v);
                return v;
            }
            if (v > alpha) {
                alpha = v;
                if (player == getColour()) {
                    lBestMove = m;
                    //if (usetable) stateTable.put(board.toString(), v);
                }
            }
        }
        //if (usetable) stateTable.put(board.toString(), v);
        
        if (plies == 0 && lBestMove != null) {
            this.bestMove = lBestMove;
        }
        if (usetable) stateTable.put(board.toString(), alpha);
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
    
    public NegamaxPlayer(PlayerType colour) {
        this(colour, DEFAULT_DEPTH, DEFAULT_SORT_DEPTH);
    }
    
    public NegamaxPlayer(PlayerType colour, int depth) {
        this(colour, depth, DEFAULT_SORT_DEPTH);
    }
    
    public NegamaxPlayer(PlayerType colour, int depth, boolean usetable) {
        this(colour, depth, DEFAULT_SORT_DEPTH);
        this.usetable = true;
    }
    
    public NegamaxPlayer(PlayerType colour, int depth, int sortDepth) {
        super(colour);
        this.searchDepth = depth; 
        this.setSortDepth(sortDepth);
        //Choose the features here
        features.add(new StoneCount(0.4));
        features.add(new LegalMoves(0.6));
    }
    
    public void setSearchDepth(int searchDepth) {
        this.searchDepth = searchDepth;
    }
    
    public int getSearchDepth() {
        return searchDepth;
    }
    
    public void setSortDepth(int sortDepth) {
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
