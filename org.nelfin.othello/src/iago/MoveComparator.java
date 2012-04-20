package iago;

import iago.Player.PlayerType;

import java.util.Comparator;

public class MoveComparator implements Comparator<Move> {
    
    private Board board;
    private PlayerType player;
    
    @Override
    public int compare(Move arg0, Move arg1) {
        return simpleScore(arg0) - simpleScore(arg1);
    }
    
    private int simpleScore(Move m) {
        return board.scoreMove(m, player);
    }
    
    public void setBoard(Board b) {
        this.board = b;
    }
    
    public void setPlayer(PlayerType p) {
        this.player = p;
    }
    
    public MoveComparator(Board board, PlayerType player) {
        this.board = board;
        this.player = player;
    }
    
    public MoveComparator(MoveComparator other) {
        this(other.board, other.player);
    }
}
