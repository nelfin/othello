package iago;

public interface Player {
    
    public enum PlayerType {
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

        public static long toInteger(PlayerType p) {
            switch (p) {
            case WHITE:
                return 0;
            case BLACK:
                return 1;
            }
            return 2;
        }

        public static PlayerType fromInteger(int readInt) {
            switch (readInt) {
            case 0:
                return WHITE;
            case 1:
                return BLACK;
            case 2:
                return NONE;
            }
            return null;
        }
    }
    
    public abstract Move chooseMove(Board board);

}