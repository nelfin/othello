package iago;

import java.util.Scanner;
import java.util.Set;

public class HumanPlayer extends AbstractPlayer {
    /* (non-Javadoc)
     * @see iago.Player#chooseMove(iago.Board)
     */
    public Move chooseMove(Board board) {
        Set<Move> legalMoves = board.validMoves(getColour());
        if (legalMoves.size() > 1) {
            System.out.println(legalMoves.size() + " moves available:");
            int count = 0;
            for (Move m : legalMoves) {
                System.out.println(count + "> (" + m.x + ", " + m.y + "), score = " +
                        board.scoreMove(m, getColour()));
                count++;
            }
            Scanner sc = new Scanner(System.in);
            boolean valid = false;
            int index = 0;
            while (!valid) {
                System.out.print("Enter your choice of move [0-" + (count-1) +"]: ");
                index = sc.nextInt();
                if (0 <= index && index < count) {
                    valid = true;
                }
            }
            return (Move) legalMoves.toArray()[index];
        } else {
            // legal Moves should be exactly 1, provided server implementation
            // matches documentation
            Move m = legalMoves.iterator().next();
            System.out.println("Only one move available: (" + m.x + ", " + m.y + ")");
            System.out.println("Making it for you.");
            return m;
        }
    }
    
    public HumanPlayer(PlayerType colour) {
        super(colour);
    }
}
