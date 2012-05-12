package iago.features;

import iago.Board;
import iago.players.Player.PlayerType;

public class ErsatzFeature extends Feature {
    
    @Override
    public Integer evaluate(Board state, PlayerType player) {
        // This shouldn't be called, this isn't an actual feature
        return null;
    }
    
    public ErsatzFeature(Feature other) {
        super(other);
    }
}
