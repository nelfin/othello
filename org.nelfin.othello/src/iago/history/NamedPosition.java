package iago.history;

import iago.Move;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class NamedPosition {
    
    private static final String SENTINEL = "$";
    
    public String name;
    
    public Map<Move, NamedPosition> children;
    
    private NamedPosition(String name) {
        this.name = name;
        this.children = new HashMap<Move, NamedPosition>();
    }
    
    public NamedPosition getNextPosition(Move m) {
        return this.children.get(m);
    }
    
    private void attachChild(Move m, NamedPosition child) {
        children.put(m, child);
    }
    
    public static NamedPosition fromStream(InputStream is) {
        Scanner sc = new Scanner(is);
        sc.useDelimiter(";");
        
        return fromScanner(sc);
    }
    
    private static NamedPosition fromScanner(Scanner sc) {
        String name = sc.next();
        NamedPosition result = new NamedPosition(name);
        
        while (sc.hasNext()) {
            String sentinelValue = sc.next().trim();
            int x;
            
            if (sentinelValue.equals(SENTINEL)) {
                return result;
            } else {
                x = Integer.parseInt(sentinelValue);
            }
            
            int y = sc.nextInt();
            //name = sc.next();
            Move m = new Move(x, y);
            NamedPosition child = fromScanner(sc);
            result.attachChild(m, child);
        }
        
        return result;
    }
}
