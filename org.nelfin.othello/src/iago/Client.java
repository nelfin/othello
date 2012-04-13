package iago;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;


public class Client {
    
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 3130;
    private static final String NAME = "Jafar";
    
    private String host;
    private int port;
    private Board.Player player;
    private Board board;
    
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
        Client client = new Client(player, host, port);
        if (!client.connect()) {
            System.err.println("[client] unable to establish a connection, exiting");
            System.exit(1);
        }
        client.runForever();
    }

    private boolean connect() {
        try {
            socket = new Socket(host, port);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        } catch (UnknownHostException e) {
            System.err.println("[client] unknown host: " + host);
            return false;
        } catch (IOException e) {
            System.err.println("[client] error creating socket");
            return false;
        }
        try {
            out.write(connectMessage());
            out.flush();
        } catch (IOException e) {
            System.err.println("[client] could not connect to server");
            return false;
        }
        return true;
    }

    private byte[] connectMessage() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream w = new DataOutputStream(buffer);
        
        w.writeInt(player.toInteger());
        w.writeInt(NAME.length());
        w.write(NAME.getBytes());
        w.flush();
        
        return buffer.toByteArray();
    }

    private void runForever() {
        while (true) {
            // receive
            if (!processServerMessage()) {
                System.err.println("[client] shutting down");
                return;
            }
            // game status should be GIVE_MOVE at this point
            playNextMove();
            sendMove();
        }
    }

    private void playNextMove() {
        nextMove = computerPlayer.chooseMove(board);
    }

    private void sendMove() {
        clientMessage.setMove(nextMove);
        try {
            clientMessage.send(out);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
            System.err.println("[client] error in receiving server message");
            return false;
        }
        
        if (serverMessage.gameAborted()) {
            System.err.println("[client] server aborted game");
            return false;
        }
        
        if (serverMessage.gameHasEnded()) {
            Board.Player winner = serverMessage.getWinner();
            if (winner == Board.Player.NONE) {
                // A draw
                System.out.println("[client] Game was a draw");
            } else if (winner == player) {
                System.out.println("[cilent] We won :)");
            } else {
                System.out.println("[client] We lost :(");
            }
            return false;
        }
        
        if (serverMessage.cantMakeMove()) {
            clientMessage.setMove(-1, -1);
            try {
                clientMessage.send(out);
            } catch (IOException e) {
                System.err.println("[client] unable to send move to server");
                return false;
            }
        }
        
        board.processMessage(serverMessage);
        
        return true;
    }

    public Client(Board.Player player, String host, int port) {
        this.player = player;
        this.host = host;
        this.port = port;
        this.board = new Board();
        this.serverMessage = new ServerMessage();
        this.clientMessage = new ClientMessage();
        this.computerPlayer = new RandomPlayer();
    }
}
