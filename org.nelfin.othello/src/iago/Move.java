package iago;

import java.awt.Point;

public class Move extends Point {
    /**
     * 
     */
    private static final long serialVersionUID = 2066875822332872611L;
    
    public static final Move NO_MOVE = new Move(-1, -1);
    
    public int score;
    
    public Move(int x, int y) {
        this(x, y, 0);
    }
    
    public Move(int x, int y, int score) {
        super(x, y);
        this.score = score;
    }
    
    public void invert() {
        this.score = -this.score;
    }
}
