package iago;

import iago.players.GreedyPlayer;
import iago.players.Player;
import iago.players.Player.PlayerType;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class Client implements Runnable {
    
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 3130;
    private static final String DEFAULT_STRATEGY = "meta";
    private static final String NAME = "Jafar";
    
    private String host;
    private int port;
    private PlayerType player;
    private boolean canMove;
    private Board board;
    private long timeRemaining;
    
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    
    private ServerMessage serverMessage;
    private ClientMessage clientMessage;
    private Player computerPlayer;
    private Move nextMove;
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        if ((args.length < 1) || (args.length > 3)) {
            System.err.println("usage: " + Client.class.getName() + " <white|black> [[host:]port] [strategy]");
            System.exit(1);
        }
        
        PlayerType player = PlayerType.NONE;
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;
        String strategy = DEFAULT_STRATEGY;
        
        if (args[0].startsWith("white")) {
            player = PlayerType.WHITE;
        } else if (args[0].startsWith("black")) {
            player = PlayerType.BLACK;
        } else {
            System.err.println("[client] invalid value for player");
            System.exit(1);
        }
        
        System.err.println("[client] player is " + player.toString());
        
        if (args.length >= 2) {
            String[] parts = args[1].split(":");
            if (parts.length == 1) {
                port = Integer.parseInt(parts[0]);
            } else {
                host = parts[0];
                port = Integer.parseInt(parts[1]);
            }
        }
        if (args.length == 3) {
            strategy = args[2]; 
        }
        
        // And, we're off!
        Player ai = null;
        try {
            ai = StrategyLookup.playerFromStrategy(strategy, player);
        } catch (IllegalArgumentException e) {
            System.err.println("[client] invalid strategy: " + strategy);
            System.exit(1);
        }
        Client mClient = new Client(player, host, port, ai);
        if (!mClient.connect()) {
            System.err.println("[client] unable to establish a connection, exiting");
            System.exit(1);
        }
        mClient.run();
    }

    private boolean connect() {
        try {
            socket = new Socket(host, port);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        } catch (UnknownHostException e) {
            report(System.err, "unknown host: " + host);
            return false;
        } catch (IOException e) {
            report(System.err, "error creating socket");
            return false;
        }
        try {
            out.write(connectMessage());
            out.flush();
        } catch (IOException e) {
            report(System.err, "could not connect to server");
            return false;
        }
        return true;
    }

    private byte[] connectMessage() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream w = new DataOutputStream(buffer);
        String name = NAME + " - " + this.player.toString();
        
        w.writeInt(player.toInteger());
        w.writeInt(name.length());
        w.write(name.getBytes());
        w.flush();
        
        return buffer.toByteArray();
    }

    private void runForever() {
        while (true) {
            // receive
            if (!processServerMessage()) {
                report(System.err, "shutting down");
                return;
            }
            // game status should be GIVE_MOVE or NO_MOVE at this point
            if (canMove) {
                playNextMove();
            } else {
                nextMove = Move.NO_MOVE;
            }
            report("computer player chose " + nextMove.toString());
            try {
                sendMove();
            } catch (IOException e) {
                report(System.err, "error sending move, shutting down");
                return;
            }
        }
    }

    private void playNextMove() {
        nextMove = computerPlayer.chooseMove(board, timeRemaining);
    }

    private void sendMove() throws IOException {
        clientMessage.setMove(nextMove);
        clientMessage.send(out);
    }

    /**
     * Receive and delegate next server message
     * 
     * @return continue
     * Whether or not execution should continue after this round 
     */
    private boolean processServerMessage() {
        try {
            serverMessage.receive(in);
        } catch (IOException e) {
            report(System.err, "error in receiving server message");
            return false;
        }
        
        if (serverMessage.gameAborted()) {
            report(System.err, "server aborted game");
            return false;
        }
        
        if (serverMessage.gameHasEnded()) {
            PlayerType winner = serverMessage.getWinner();
            if (winner == PlayerType.NONE) {
                // A draw
                report("Game was a draw");
            } else if (winner == player) {
                report("We won :)");
            } else {
                report("We lost :(");
            }
            return false;
        }
        
        canMove = !serverMessage.cantMakeMove();
        
        board.processMessage(serverMessage);
        timeRemaining = serverMessage.getTimeRemaining();
        
        return true;
    }
    
    public Client(PlayerType player, String host, int port) {
        this(player, host, port, new GreedyPlayer(player));
    }
    
    public Client(PlayerType player, String host, int port, Player ai) {
        this.player = player;
        this.host = host;
        this.port = port;
        this.canMove = true;
        this.board = new Board();
        this.serverMessage = new ServerMessage();
        this.clientMessage = new ClientMessage();
        this.computerPlayer = ai;
    }

    private void report(String message) {
        report(System.out, message);
    }
    private void report(PrintStream f, String message) {
        f.println("["+this.player.toString()+"] " + message);
    }
    
    @Override
    public void run() {
        this.runForever();
    }
}
