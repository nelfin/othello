package iago;

public class NegamaxPlayerTest extends SmartPlayerTest {
	protected void setUp() {
		smartWhitePlayer = new NegamaxPlayer(Player.PlayerType.WHITE, 8, 0);
		smartBlackPlayer = new NegamaxPlayer(Player.PlayerType.BLACK, 8, 0);
		greedyPlayer = new NegamaxPlayer(Player.PlayerType.WHITE, 1);
    }
	
	
}
