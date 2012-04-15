package iago;

import java.awt.Point;

public class Move extends Point {
    /**
     * 
     */
    private static final long serialVersionUID = 2066875822332872611L;
    
    public static final Move NO_MOVE = new Move(-1, -1);
    
    public Move(int x, int y) {
        super(x, y);
    }
}
