package iago;
import iago.players.AlphaBetaPlayer;
import iago.players.Player;

import org.junit.Test;


public class AlphaBetaPlayerTest extends SmartPlayerTest {
    protected void setUp() {
        greedyPlayer = new AlphaBetaPlayer(Player.PlayerType.WHITE, 1);
        smartWhitePlayer = new AlphaBetaPlayer(Player.PlayerType.WHITE, 8);
        smartBlackPlayer = new AlphaBetaPlayer(Player.PlayerType.BLACK, 8);
    }
    
    @Test
    public void testSetSearchDepth() {
        AlphaBetaPlayer abPlayer = new AlphaBetaPlayer(Player.PlayerType.WHITE);
        abPlayer.setSearchDepth(2);
        assertEquals(abPlayer.getSearchDepth(), 2);

    }

    @Test
    public void testGetSearchDepth() {
        AlphaBetaPlayer abPlayer = new AlphaBetaPlayer(Player.PlayerType.WHITE, 3);
        assertEquals(abPlayer.getSearchDepth(), 3);
    }
    
    @Test
    public void testChooseMoveBestLine() {
        AlphaBetaPlayer abPlayer = new AlphaBetaPlayer(Player.PlayerType.WHITE, 1);
        char[][] testBoardData = DebugFunctions.makeSolidBoardCharArray('.');

        testBoardData[0][0] = 'w';
        testBoardData[0][1] = 'b';
        testBoardData[0][2] = 'b';
        
        testBoardData[3][0] = 'w';
        testBoardData[3][1] = 'b';
        testBoardData[3][2] = 'b';
        testBoardData[3][3] = 'b';
        Board testBoard = new Board(DebugFunctions.charArrayToBoardString(testBoardData));
        
        assertEquals(abPlayer.chooseMove(testBoard), new Move(3,4));
    }
}
