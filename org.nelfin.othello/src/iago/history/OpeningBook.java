package iago.history;
import java.util.Set;
import iago.*;

public class OpeningBook {
	private NamedPosition initialPosition;
	private NamedPosition currentOpening;
	
	public OpeningBook() {
		initialPosition = NamedPosition.fromStream(OpeningBookLoader.class.getResourceAsStream("small_book"));
		currentOpening = initialPosition;
	}
	
	//In between games we need to reset the opening book
	public void reset(){
		currentOpening = initialPosition;
	}
	
	
	/**
	 * Returns an opening move if there is one, but gives up if there isn't
	 */
	public Move getNextOpeningMove(Move otherPlayersMove){
		if(otherPlayersMove == null){ //The first move in the game
			//Reset the current position
			currentOpening = initialPosition;
			return (Move) initialPosition.children.keySet().toArray()[0];
		}
		if(!currentOpening.children.containsKey(otherPlayersMove)){
			return Move.NO_MOVE;
		}
		currentOpening = currentOpening.getNextPosition(otherPlayersMove);
		boolean noReply = currentOpening.children.keySet().size() == 0;
		if(noReply){
			return Move.NO_MOVE;
		}else{
			Move moveToPlay =  (Move) currentOpening.children.keySet().toArray()[0];
			currentOpening = currentOpening.getNextPosition(moveToPlay);
			System.out.println("Playing the opening: "+ currentOpening.name);
			return moveToPlay;
		}
		
		
	}
    
}
