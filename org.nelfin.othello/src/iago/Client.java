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
            this.socket = new Socket(this.host, this.port);
            this.out = new DataOutputStream(this.socket.getOutputStream());
            this.in = new DataInputStream(this.socket.getInputStream());
        } catch (UnknownHostException e) {
            System.err.println("[client] unknown host: " + this.host);
            return false;
        } catch (IOException e) {
            System.err.println("[client] error creating socket");
            return false;
        }
        try {
            this.out.write(connectMessage());
            this.out.flush();
        } catch (IOException e) {
            System.err.println("[client] could not connect to server");
            return false;
        }
        return true;
    }

    private byte[] connectMessage() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream w = new DataOutputStream(buffer);
        
        w.writeInt(this.player.toInteger());
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
            // handle
            // send
        }
    }

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
            // TODO check if NONE is returned for draw
            if (winner == Board.Player.NONE) {
                // A draw
                System.out.println("[client] Game was a draw");
            } else if (winner == this.player) {
                System.out.println("[cilent] We won :)");
            } else {
                System.out.println("[client] We lost :(");
            }
            return false;
        }
        
        if (serverMessage.cantMakeMove()) {
            // TODO send (-1, -1)
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
    }
}
