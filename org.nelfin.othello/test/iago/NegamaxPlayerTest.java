package iago;

public class NegamaxPlayerTest extends SmartPlayerTest {
	protected void setUp() {
		smartWhitePlayer = new NegamaxPlayer(Player.PlayerType.WHITE, 8);
		smartBlackPlayer = new NegamaxPlayer(Player.PlayerType.BLACK, 8);
		greedyPlayer = new NegamaxPlayer(Player.PlayerType.WHITE, 1);
    }
	
	
}
