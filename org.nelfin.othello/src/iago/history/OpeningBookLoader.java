package iago.history;

import iago.Move;

public class OpeningBookLoader {
    
    public static void main(String[] args) {
        NamedPosition initialPosition =
            NamedPosition.fromStream(
                OpeningBookLoader.class.getResourceAsStream("small_book"));
        System.out.println(initialPosition.name);
        for (Move m: initialPosition.children.keySet()) {
            System.out.println(m.toString() + ": " + initialPosition.getNextPosition(m).name);
            for (Move m2: initialPosition.getNextPosition(m).children.keySet()) {
                System.out.println("\t"+m2.toString() + ": " + initialPosition.getNextPosition(m).getNextPosition(m2).name);
            }
        }
    }
}
