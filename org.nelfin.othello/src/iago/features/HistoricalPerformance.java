package iago.features;

import iago.Board;

public class HistoricalPerformance extends Feature {
    
    public HistoricalPerformance(double weight) {
        super("Opening Book",
              "Gives scores based on expected win/loss/draw from a standard " +
              "opening book",
              weight);
    }
    /* (non-Javadoc)
     * @see iago.features.Feature#evaluate(iago.Board)
     */
    @Override
    public Integer evaluate(Board state) {
        // Given this board state, how likely are we to win given our
        // historical performance?
        return 0;
    }

}
