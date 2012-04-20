package iago;

public class NegamaxPlayerTest extends PlayerTest {
	protected void setUp() {
		p = new NegamaxPlayer(Player.PlayerType.WHITE, NegamaxPlayer.DEFAULT_DEPTH);
    }
}
