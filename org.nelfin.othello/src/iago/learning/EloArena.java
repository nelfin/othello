package iago.learning;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import iago.Board;
import iago.Move;
import iago.players.EloSet;
import iago.players.MetaPlayer;
import iago.players.Player.PlayerType;

public class EloArena {
	static final int BLOCKED_COUNT=4; //TODO: move this
	static final int LEARNING_ITERATIONS=1000000;//ONE MILLION
	static final int RUNNING_WIN_LOSS_SIZE=50; //We get the win loss over the past RUNNING_WIN_LOSS_SIZE games
	static final int LOG_SAVE_COUNT = 100; //Saves the file every LOG_SAVE_COUNT games
	static final String LOG_DIRECTORY = "EloLogs";
	static final int DEPTH = 2;
	static final int MAX_CHAMPS = 20;
	static final int MIN_CHAMPS = 5;
	static final int DEFAULT_ELO = 1200;
	static final int GAMES_PER_PAIRING = 5;
	
	static ArrayList<EloSet> champs= new ArrayList<EloSet>();
	static ArrayList<String> names= new ArrayList<String>();
	
	static Random r = new Random();
	
	public static void main(String[] args)
	{
		loadArena();
		loadNames();
		while (champs.size() < MIN_CHAMPS)
			addRandomChamp();
		for(int a = 0; a < LEARNING_ITERATIONS; a++){
			for (int c1=0; c1<champs.size(); c1++) 
				for (int c2=c1+1; c2<champs.size(); c2++) 
					for (int i=0; i<GAMES_PER_PAIRING; i++) {
						//System.out.println("=====================");
						//System.out.println("Playing Game between " + c1 + " and " + c2);
						//System.out.println("=====================");
						playGame(c1, c2);
						saveArena();
					}
			sortChamps();
			addScaledChamp();
			if (champs.size() > MAX_CHAMPS) trimChamps();
			saveArena();
		}
	}
	
	private static void addRandomChamp() {
		String name = names.get(r.nextInt(names.size()));
		int edition = 0;
		while ((new File(LOG_DIRECTORY+"/"+name+edition)).exists()) edition++;
		
		EloSet champ = new EloSet(DEFAULT_ELO, LOG_DIRECTORY+"/"+name+edition);
		champ.makeRandomWeights();
		champs.add(champ);
	}
	
	private static void addScaledChamp() {
		String name = names.get(r.nextInt(names.size()));
		int edition = 0;
		while ((new File(LOG_DIRECTORY+"/"+name+edition)).exists()) edition++;
		
		EloSet champ = new EloSet(DEFAULT_ELO, LOG_DIRECTORY+"/"+name+edition);
		System.out.println("New champ " + name + " enters the fray!!");
		champ.makeScaledWeights(champs);
		//Insert at front so it won't be immediately trimmed
		champs.add(0,champ);
	}
	
	private static void sortChamps() {
		Collections.sort(champs);
		Collections.reverse(champs);
	}
	
	private static void trimChamps() {
		while (champs.size() > MAX_CHAMPS) champs.remove(champs.size()-1);
	}
	
	private static void playGame(int c1, int c2) {
		MetaPlayer black;
		MetaPlayer white;
		double feedback = 0;
		EloSet champ1 = champs.get(c1);
		EloSet champ2 = champs.get(c2);
		String initialBoardRepresentation = PracticeArena.generateRandomBoard();
		//We want the player to play from both sides
		for(int side = 0; side <= 1; side++) //side==0 means Learner is playing white
		{
			//Set up a game
			Board board = new Board(initialBoardRepresentation);
			boolean whiteTurn;
			if(side==0){
				white = new MetaPlayer(PlayerType.WHITE,DEPTH,champ1);
				black = new MetaPlayer(PlayerType.BLACK,DEPTH,champ2);
				whiteTurn = true;
			}else{
				white = new MetaPlayer(PlayerType.WHITE,DEPTH,champ2);
				black = new MetaPlayer(PlayerType.BLACK,DEPTH,champ1);
				whiteTurn = false;
			}
			Move nextMove = new Move(0,0);
			
			//Start playing the game
			int consecutivePasses = 0;
			while(consecutivePasses < 2)
			{		
				if(!whiteTurn)
				{
					nextMove = white.chooseMove(board);
					//apply the move
					board.apply(nextMove, PlayerType.WHITE, true);

				}else{
					nextMove = black.chooseMove(board);
					//apply the move
					board.apply(nextMove, PlayerType.BLACK, true);
				}
				whiteTurn = !whiteTurn;
				
				//For figuring out if the game is over
				if(nextMove.equals(new Move(-1,-1))){
					consecutivePasses++;
				}else{
					consecutivePasses = 0;
				}
			}
			int boardScore = board.scoreBoard(PlayerType.WHITE);
			
			boolean whiteWins = boardScore > 0;
			boolean tie = boardScore == 0;
			double thisGameFeedback = 0.0;
			
			
			//Allocate points
			if(whiteWins && side==0 && !tie){ //champ1 won while white
				thisGameFeedback = 1.0 / 2; // div 2 since we're going to play from both sides
			}
			if(!whiteWins && side==1 && !tie){ //champ1 won while black
				thisGameFeedback = 1.0 / 2; 
			}
			if(tie){
				thisGameFeedback = 0.5 / 2;
			}
			feedback += thisGameFeedback;
			//System.out.println("Feedback: "+thisGameFeedback);
		}
		int diff1 = champ1.updateELO(champ2.getELO(),   feedback);
		int diff2 = champ2.updateELO(champ1.getELO(), 1-feedback);
		//System.out.println("Player " + c1 + " with ELO " + champ1.getELO() + " modified by " + diff1);
		//System.out.println("Player " + c2 + " with ELO " + champ2.getELO() + " modified by " + diff2);
		champ1.standardiseWeights();
		champ1.save();
		champ1.saveToFile();
		champ2.standardiseWeights();
		champ2.save();
		champ2.saveToFile();
	}
	
	private static void loadArena() {
		try {
			//Make a directory for the logs if it doesn't exist
			try{new File(LOG_DIRECTORY).mkdir();}
			catch (Exception e){//Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}
			BufferedReader br = new BufferedReader(new FileReader(LOG_DIRECTORY+"/EloLogs"));
			String playerLn;
			String playerName;
			int playerElo;
			while ((playerLn = br.readLine()) != null)   {
				playerName   = playerLn.split(":")[0];
				playerElo = Integer.parseInt(playerLn.split(":")[1]);
				champs.add(new EloSet(playerElo, playerName));
			}
		} catch (FileNotFoundException e) {
			System.out.println("No ELO logs found. Starting from scratch");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void loadNames() {
		try {
			//Make a directory for the logs if it doesn't exist
			try{new File(LOG_DIRECTORY).mkdir();}
			catch (Exception e){//Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}
			BufferedReader br = new BufferedReader(new FileReader(LOG_DIRECTORY+"/champs"));
			String name;
			while ((name = br.readLine()) != null)   {
				names.add(name);
			}
		} catch (FileNotFoundException e) {
			System.out.println("Names list not found!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void saveArena() {
		//Make a directory for the logs if it doesn't exist
		try{new File(LOG_DIRECTORY).mkdir();}
		catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(LOG_DIRECTORY+"/EloLogs"));
			for (EloSet c: champs)
				bw.write(c.getName() + ":" + Integer.toString(c.getELO()) + "\n");
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
