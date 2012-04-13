package iago;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClientMessage {

    private Move move;
    
    public ClientMessage() {
        this(-1, -1);
    }
    
    public ClientMessage(int x, int y) {
        this.move = new Move(x, y);
    }
    
    public void setMove(int x, int y) {
        this.move.setLocation(x, y);
    }
    
    public void setMove(Move nextMove) {
        this.move = nextMove;
    }
    
    public void send(DataOutputStream pipe) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream w = new DataOutputStream(buffer);
        
        try {
            w.writeInt(this.move.x);
            w.writeInt(this.move.y);
            w.flush();
        } catch (IOException e) {
            // This would only happen with out-of-memory AFAIK
            e.printStackTrace();
        }
        
        pipe.write(buffer.toByteArray());
    }

}
