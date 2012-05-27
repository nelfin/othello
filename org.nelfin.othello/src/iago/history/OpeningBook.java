package iago.history;
import java.util.Set;
import iago.*;

public class OpeningBook {
	NamedPosition initialPosition;
	NamedPosition currentOpening;
	public OpeningBook() {
		initialPosition = NamedPosition.fromStream(OpeningBookLoader.class.getResourceAsStream("small_book"));
		currentOpening = initialPosition;
	}
	
	
	
	public Move getNextOpeningMove(Move otherPlayersMove){
		if(otherPlayersMove == Move.NO_MOVE){ //The first move in the game
			return (Move) initialPosition.children.keySet().toArray()[0];
		}
		currentOpening = currentOpening.getNextPosition(otherPlayersMove);
		if(currentOpening == null){
			return Move.NO_MOVE;
		}
		
		Move moveToPlay =  (Move) currentOpening.children.keySet().toArray()[0];
		currentOpening = currentOpening.getNextPosition(moveToPlay);
		System.out.println("Playing the opening: "+ currentOpening.name);
		return moveToPlay;
	}
    
}
