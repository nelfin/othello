package iago.players;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import iago.Board;
import iago.Move;
import iago.features.*;
import iago.players.Player.PlayerType;

public class MetaPlayer extends AbstractPlayer{
	NegamaxPlayer negamaxPlayer;
	OpeningBookPlayer openingBookPlayer;
	
	private void instantiateOpeningBookPlayer(PlayerType colour){
		openingBookPlayer = new OpeningBookPlayer(colour);
	}
	public MetaPlayer(PlayerType colour) {
		super(colour);
		negamaxPlayer = new NegamaxPlayer(colour);
		instantiateOpeningBookPlayer(colour);
		
    }
    
    public MetaPlayer(PlayerType colour, int depth) {
    	super(colour);
		negamaxPlayer = new NegamaxPlayer(colour, depth);
		instantiateOpeningBookPlayer(colour);
    }

	@Override
	public Move chooseMove(Board board) {
		Move openingMove = openingBookPlayer.chooseMove(board);
		if(openingMove != Move.NO_MOVE){ //Also make sure that it's a legal move (blocked spaces)
			return openingMove;
		}else{
			return negamaxPlayer.chooseMove(board);
		}
	}
	
	

}
