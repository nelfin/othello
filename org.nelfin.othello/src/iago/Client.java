package iago;


public class Client {
    
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 3130;
    
    private String host;
    private int port;
    private Board.Player player;
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        if ((args.length < 1) || (args.length > 2)) {
            System.err.println("usage: " + Client.class.getName() + " <white|black> [[host:]port]");
            System.exit(1);
        }
        
        Board.Player player = Board.Player.NONE;
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;
        
        if (args[0].startsWith("white")) {
            player = Board.Player.WHITE;
        } else if (args[0].startsWith("black")) {
            player = Board.Player.BLACK;
        } else {
            System.err.println("[client] invalid value for player");
            System.exit(1);
        }
        
        System.err.println("[client] player is " + player.toString());
        
        if (args.length == 2) {
            String[] parts = args[1].split(":");
            if (parts.length == 1) {
                port = Integer.parseInt(parts[0]);
            } else {
                host = parts[0];
                port = Integer.parseInt(parts[1]);
            }
        }
        
        // And, we're off!
        @SuppressWarnings("unused")
        Client client = new Client(player, host, port);
    }

    public Client(Board.Player player, String host, int port) {
        this.setPlayer(player);
        this.setHost(host);
        this.setPort(port);
    }

    public void setPlayer(Board.Player player) {
        this.player = player;
    }

    public Board.Player getPlayer() {
        return player;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }
    
}
