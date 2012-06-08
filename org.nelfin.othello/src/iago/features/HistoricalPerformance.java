package iago.features;

import iago.Board;
import iago.players.Player.PlayerType;

/**
 * Gives scores based on expected win/loss/draw from a standard opening book
 * Past performance may indicate potential for future performance, or
 * principal variations.
 * 
 * XXX: Not implemented
 *
 */
public class HistoricalPerformance extends Feature {
    
    public HistoricalPerformance(double weight) {
        super("Opening Book",
              "Gives scores based on expected win/loss/draw from a standard " +
              "opening book",
              weight, 1);
    }

    /* (non-Javadoc)
     * @see iago.features.Feature#evaluate(iago.Board)
     */
	@Override
	public Integer evaluate(Board state, PlayerType player) {
		// Given this board state, how likely are we to win given our
        // historical performance?
        return 0;
	}

}
