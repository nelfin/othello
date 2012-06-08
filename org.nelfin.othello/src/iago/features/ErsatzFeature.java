package iago.features;

import iago.Board;
import iago.players.Player.PlayerType;

/**
 * "Fake" feature storing only a weight and name. Utilised by TD-lamda
 * learning in LearningPlayer to construct weight deltas.
 *
 */
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
