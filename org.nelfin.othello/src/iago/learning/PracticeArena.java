package iago.learning;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import iago.Board;
import iago.DebugFunctions;
import iago.Move;
import iago.players.AlphaBetaPlayer;
import iago.players.GreedyPlayer;
import iago.players.LearningPlayer;
import iago.players.MetaPlayer;
import iago.players.NegamaxPlayer;
import iago.players.Player;
import iago.players.Player.PlayerType;

public class PracticeArena{
    static final int BLOCKED_COUNT=4; //TODO: move this
    static final int LEARNING_ITERATIONS=100000;//ONE MILLION
    static final int RUNNING_WIN_LOSS_SIZE=1000; //We get the win loss over the past RUNNING_WIN_LOSS_SIZE games
    // Flush log file if last save was more than LOG_SAVE_MILLIS ago
    private static final long LOG_SAVE_MILLIS = 5*60*1000;
    static final String LOG_DIRECTORY = "LearningLogs";
    // Higher values of ALPHA => greater discount on older values of feedback
    private static final double ALPHA = 0.05;
    private static final int DEPTH = 6;
    
    private static Writer allWinLossLog;
        
    public static void main(String[] args)
    {
        Player blackOpponent = new AlphaBetaPlayer(PlayerType.BLACK, DEPTH);//new MetaPlayer(PlayerType.BLACK, DEPTH);
        Player whiteOpponent = new AlphaBetaPlayer(PlayerType.BLACK, DEPTH);//new MetaPlayer(PlayerType.WHITE, DEPTH);
        //This is the learning player. They could both learn, but it's easy to reference them this way
        LearningPlayer whiteLearner = new MetaPlayer(PlayerType.WHITE, DEPTH); 
        LearningPlayer blackLearner = new MetaPlayer(PlayerType.BLACK, DEPTH); 
        
        double cumAvg = 0.0;
        double expMovAvg = 0.0;
        long lastSaveTime = System.currentTimeMillis();
        
        //Start our history file
        try{
            /**<META CODE>**/
            //Make a directory for the logs if it doesn't exist
            try{new File(LOG_DIRECTORY).mkdir();}
            catch (Exception e){//Catch exception if any
                System.err.println("Error: " + e.getMessage());
            }
            // Create file 
            FileWriter longStream = new FileWriter(LOG_DIRECTORY+"/LearningHistory.csv");
            allWinLossLog = new BufferedWriter(longStream);
            allWinLossLog.write("Iteration,Cumulative Average,Exponential Moving Average\n");
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    System.out.println("Closing arena log.");
                    try {
                        allWinLossLog.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            /**</META CODE>**/
            
            for(int a = 0; a < LEARNING_ITERATIONS; a++){
                double feedback = 0;
                System.out.println("=====================");
                System.out.println("Learning iteration "+(a+1));
                System.out.println("=====================");
    
                int side = 0;
                LearningPlayer learner;
                Player opponent;
                String initialBoardRepresentation = generateRandomBoard();
                //We want the player to play from both sides
                for(side = 0; side <= 1; side++) //side==0 means Learner is playing white
                {
                    System.out.println("Playing game "+(a+1)+"/"+LEARNING_ITERATIONS+((side==0)?"":" reversed"));
                    
                    //Set up a game
                    Board board = new Board(initialBoardRepresentation);
                    boolean learnerTurn;
                    if(side==0){
                        learner = whiteLearner;
                        opponent = blackOpponent;
                        learnerTurn = true;
                    }else{
                        learner = blackLearner;
                        opponent = whiteOpponent;
                        learnerTurn = false;
                    }
                    Move nextMove = new Move(0,0);
                    
                    //Start playing the game
                    int consecutivePasses = 0;
                    while(consecutivePasses < 2)
                    {       
                        if(!learnerTurn)
                        {
                            nextMove = opponent.chooseMove(board);
                            //apply the move
                            board.apply(nextMove, (side==0)?PlayerType.BLACK:PlayerType.WHITE, true);
    
                        }else{
                            nextMove = learner.chooseMove(board);
                            //apply the move
                            board.apply(nextMove, (side==0)?PlayerType.WHITE:PlayerType.BLACK, true);
                        }
                        learnerTurn = !learnerTurn;
                        
                        //For figuring out if the game is over
                        if(nextMove.equals(Move.NO_MOVE)){
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
                    if(whiteWins && side==0 && !tie){ //Learner won while white
                        thisGameFeedback = 1.0 / 2; // div 2 since we're going to play from both sides
                    }
                    if(!whiteWins && side==1 && !tie){ //Learner won while black
                        thisGameFeedback = 1.0 / 2; 
                    }
                    if(tie){
                        thisGameFeedback = 0.5 / 2;
                    }
                    feedback += thisGameFeedback;
                    System.out.println("Feedback: "+thisGameFeedback);
                }
                //Learner
                //Improve our feature weights
                whiteLearner.receiveFeedback(feedback);
                //Both players can learn, and we can check out the weights for playing from both sides separately
                blackLearner.receiveFeedback(feedback);
//              //Opponent learns too
//              whiteOpponent.receiveFeedback(feedback);
//              blackOpponent.receiveFeedback(feedback);
                
                System.out.println("White: "+whiteLearner.getFeatureSet());
                System.out.println("Black: "+blackLearner.getFeatureSet());
                
                
                /**<META CODE>**/
                //Update the win/loss log
                cumAvg = (feedback + a * cumAvg) / (a + 1);
                if (a == 0) {
                    expMovAvg = feedback;
                } else {
                    expMovAvg = ALPHA * feedback + (1 - ALPHA) * expMovAvg;
                }
                allWinLossLog.write(a + "," + cumAvg + "," + expMovAvg + "\n");
                long elapsedTime = System.currentTimeMillis() - lastSaveTime;
                if (elapsedTime > LOG_SAVE_MILLIS) {
                    allWinLossLog.flush();
                    lastSaveTime = System.currentTimeMillis();
                }
                /**</META CODE>**/
                
            }
            
        System.out.println("Done");
        
        // Output stream is closed automatically
        }catch (Exception e){//Catch exception if any
              System.err.println("Error: " + e.getMessage());
        }
    }

    
    private static String generateRandomBoard()
    {
        char[][] randomBoardRepresentation = DebugFunctions.makeSolidBoardCharArray('.');

        
        //Block positions
        HashSet<int[]> possibleBlockedPositions = new HashSet<int[]>();
        HashSet<int[]> impossibleBlockedPositions = new HashSet<int[]>();
        impossibleBlockedPositions.add(new int[]{4,4});
        impossibleBlockedPositions.add(new int[]{4,5});
        impossibleBlockedPositions.add(new int[]{5,4});
        impossibleBlockedPositions.add(new int[]{5,5});

        for(int x = 0; x < Board.BOARD_SIZE; x++){
            for(int y = 0; y < Board.BOARD_SIZE; y++){
                //Make a list of positions we might block
                int[] thisPosition = new int[]{x,y};
                if(!impossibleBlockedPositions.contains(thisPosition))
                {
                    possibleBlockedPositions.add(thisPosition);
                }
            }
        }
        for(int i = 0; i < BLOCKED_COUNT;i++)
        {
            Random random = new Random();
            int blockIndex = random.nextInt(possibleBlockedPositions.size());
            int[] positionToBlock = (int[]) possibleBlockedPositions.toArray()[blockIndex];
            possibleBlockedPositions.remove(positionToBlock); //We don't want to choose this one twice
            randomBoardRepresentation[positionToBlock[1]][positionToBlock[0]] = '*';
        }
        //Add the starting positions
        randomBoardRepresentation[4][4] = 'w';
        randomBoardRepresentation[5][5] = 'w';
        randomBoardRepresentation[4][5] = 'b';
        randomBoardRepresentation[5][4] = 'b';
        
        
        return (DebugFunctions.charArrayToBoardString(randomBoardRepresentation));
    }

}
