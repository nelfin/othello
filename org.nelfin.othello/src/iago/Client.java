package iago;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;


public class Client {
    
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 3130;
    private static final String NAME = "Jafar";
    
    private String host;
    private int port;
    private Board.Player player;
    private Socket socket;
    private DataOutputStream out;
    private BufferedReader in;
    
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
            this.in = new BufferedReader(new InputStreamReader(
                    this.socket.getInputStream()));
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
        // TODO Auto-generated method stub
        
    }

    }

    public Client(Board.Player player, String host, int port) {
        this.player = player;
        this.host = host;
        this.port = port;
        this.board = new Board();
    }
}
