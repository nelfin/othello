package iago.history;

import iago.Move;

import java.io.Serializable;
import java.util.Map;

public class NamedPosition implements Serializable {
    
    public String name;
    
    private Map<Move, NamedPosition> children;
    
    public NamedPosition getNextPosition(Move m) {
        return this.children.get(m);
    }
}
