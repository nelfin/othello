package iago;

public class Board {
    
    public static final int BOARD_SIZE = 10;
    
    private enum BoardState {
        EMPTY, WHITE, BLACK, BLOCKED;
        
        private static BoardState fromByte(byte b) {
            switch (b) {
            case 0:
                return EMPTY;
            case 1:
                return WHITE;
            case 2:
                return BLACK;
            case 3:
                return BLOCKED;
            }
            return null;
        }
    }
    
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

        public static Player fromInteger(int readInt) {
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
    
    private BoardState[][] board;
    
    public Board() {
        this.board = new BoardState[BOARD_SIZE][BOARD_SIZE];
        
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                this.board[i][j] = BoardState.EMPTY; 
            }
        }
    }
    
    public void processMessage(ServerMessage m) {
        // TODO should this be less coupled?
        byte[] boardArray = m.getBoardArray();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                this.board[i][j] = getState(boardArray, i, j);
            }
        }
    }

    private BoardState getState(byte[] boardArray, int x, int y) {
        return BoardState.fromByte(boardArray[x*BOARD_SIZE + y]);
    }
}
