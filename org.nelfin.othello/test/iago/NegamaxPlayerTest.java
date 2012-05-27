package iago;

import iago.players.NegamaxPlayer;
import iago.players.Player;

public class NegamaxPlayerTest extends SmartPlayerTestAbstract {
	protected void setUp() {
		smartWhitePlayer = new NegamaxPlayer(Player.PlayerType.WHITE, 8, true);
		smartBlackPlayer = new NegamaxPlayer(Player.PlayerType.BLACK, 8, true);
		greedyPlayer = new NegamaxPlayer(Player.PlayerType.WHITE, 1, true);
    }
	
	
}
