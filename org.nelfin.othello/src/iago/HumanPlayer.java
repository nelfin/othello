package iago;

import java.util.Scanner;
import java.util.Set;

public class HumanPlayer extends AbstractPlayer {
    /* (non-Javadoc)
     * @see iago.Player#chooseMove(iago.Board)
     */
    public Move chooseMove(Board board) {
        this.game.update(board);
        Set<Move> legalMoves = board.validMoves(getColour());
        if (legalMoves.size() == 1) {
            // legal Moves should be exactly 1, provided server implementation
            // matches documentation
            Move m = legalMoves.iterator().next();
            System.out.println("Only one move available: (" + m.x + ", " + m.y + ")");
            System.out.println("Making it for you.");
            return m;
        } else {
            return this.game.pollForMove();
        }
    }
    
    private GameScreen game;
    
    public HumanPlayer(PlayerType colour) {
        super(colour);
        this.game = new GameScreen(colour);
        this.game.draw();
        this.game.blit();
    }
}
