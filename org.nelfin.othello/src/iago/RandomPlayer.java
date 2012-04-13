package iago;

public class RandomPlayer implements Player {
    /* (non-Javadoc)
     * @see iago.Player#chooseMove(iago.Board)
     */
    public Move chooseMove(Board board) {
        return new Move(-1, -1);
    }
}
