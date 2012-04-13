package iago;

public abstract class AbstractPlayer implements Player {
    private PlayerType colour;
    
    public AbstractPlayer(PlayerType colour) {
        this.colour = colour;
    }
    
    public PlayerType getColour() {
        return colour;
    }
}
