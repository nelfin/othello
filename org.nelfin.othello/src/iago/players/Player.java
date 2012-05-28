package iago.players;

import iago.Board;
import iago.Move;

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
        
        public static PlayerType getOpponent(PlayerType p) {
            switch (p) {
            case WHITE:
                return BLACK;
            case BLACK:
                return WHITE;
            }
            return NONE;
        }
        
        public PlayerType getOpponent() {
            switch (this) {
            case WHITE:
                return BLACK;
            case BLACK:
                return WHITE;
            }
            return NONE;
        }
    }
    
    public abstract Move chooseMove(Board board);
    /**
     * @param board Current board
     * @param deadline Time in millis
     * @return Chosen move
     */
    public abstract Move chooseMove(Board board, long deadline);

}