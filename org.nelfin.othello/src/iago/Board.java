package iago;

public class Board {
    public static enum Player {
        WHITE, BLACK, NONE;
        
        public int toInteger() {
            switch (this) {
            case WHITE:
                return 0;
            case BLACK:
                return 1;
            }
            return 2;
        }

        public static long toInteger(Player p) {
            switch (p) {
            case WHITE:
                return 0;
            case BLACK:
                return 1;
            }
            return 2;
        }
    }
    
}
