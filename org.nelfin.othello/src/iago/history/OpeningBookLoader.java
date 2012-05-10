package iago.history;

import iago.Move;

public class OpeningBookLoader {
    
    public static void main(String[] args) {
        NamedPosition initialPosition =
            NamedPosition.fromStream(
                OpeningBookLoader.class.getResourceAsStream("tiny_book"));
        System.out.println(initialPosition.name);
        for (Move m: initialPosition.children.keySet()) {
            System.out.println(m.toString() + ": " + initialPosition.getNextPosition(m).name);
        }
    }
    
}
