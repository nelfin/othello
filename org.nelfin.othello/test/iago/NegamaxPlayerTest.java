package iago;

import iago.players.NegamaxPlayer;
import iago.players.Player;

public class NegamaxPlayerTest extends SmartPlayerTestAbstract {
	protected void setUp() {
		smartWhitePlayer = new NegamaxPlayer(Player.PlayerType.WHITE, 8, 0);
		smartBlackPlayer = new NegamaxPlayer(Player.PlayerType.BLACK, 8, 0);
		greedyPlayer = new NegamaxPlayer(Player.PlayerType.WHITE, 1);
    }
	
	
}
