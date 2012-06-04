package iago.players;

import iago.Board;
import iago.Move;

public abstract class AbstractPlayer implements Player {
    private PlayerType colour;
    
    public AbstractPlayer(PlayerType colour) {
        this.colour = colour;
    }
    
    public PlayerType getColour() {
        return colour;
    }
    
    public Move chooseMove(Board board, long deadline) {
        return chooseMove(board);
    }
}
