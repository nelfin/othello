package iago;

import iago.Player.PlayerType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Board {
    
    public static final int BOARD_SIZE = 10;
    private static final int BLOCKED_NUM = 4;
    
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
        
        private static BoardState asBoardState(PlayerType p) {
            switch (p) {
            case WHITE:
                return WHITE;
            case BLACK:
                return BLACK;
            }
            return null;
        }
    }
    
    private BoardState[][] board;
    private int movesPlayed;
    private Map<BoardState, Integer> cellCount;
    
    public Board() {
        this.board = new BoardState[BOARD_SIZE][BOARD_SIZE];
        this.movesPlayed = 0;
        this.cellCount = new HashMap<BoardState, Integer>();
        
        for (int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
                set(x, y, BoardState.EMPTY); 
            }
        }
        this.cellCount.put(BoardState.BLACK, 0);
        this.cellCount.put(BoardState.WHITE, 0);
    }
    
    public Board(Board board2) {
        this.board = new BoardState[BOARD_SIZE][BOARD_SIZE];
        this.movesPlayed = board2.movesPlayed;
        this.cellCount = new HashMap<BoardState, Integer>();
        for (int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
                set(x, y, board2.get(x, y)); 
            }
        }
        this.cellCount.put(BoardState.BLACK, board2.cellCount.get(BoardState.BLACK));
        this.cellCount.put(BoardState.WHITE, board2.cellCount.get(BoardState.WHITE));
    }
    
    public void processMessage(ServerMessage m) {
        // TODO should this be less coupled?
        byte[] boardArray = m.getBoardArray();
        int blackCount = 0;
        int whiteCount = 0;
        for (int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
                BoardState b = getState(boardArray, x, y);
                if (b == BoardState.BLACK) {
                    blackCount++;
                } else if (b == BoardState.WHITE) {
                    whiteCount++;
                }
                set(x, y, b);
            }
        }
        this.movesPlayed = blackCount + whiteCount;
        setCellCount(BoardState.BLACK, blackCount);
        setCellCount(BoardState.WHITE, whiteCount);
    }
    
    private void setCellCount(BoardState b, int count) {
        this.cellCount.put(b, count);
    }
    
    private void addCellCount(BoardState b, int count) {
        this.cellCount.put(b, count + this.cellCount.get(b));
    }
    
    private void subtractCellCount(BoardState b, int count) {
        this.cellCount.put(b, this.cellCount.get(b) - count);
    }
    
    public void visualise() {
        System.out.println("  0 1 2 3 4 5 6 7 8 9");
        for (int y = 0; y < BOARD_SIZE; y++) {
            System.out.printf("%d ", y);
            for (int x = 0; x < BOARD_SIZE; x++) {
                BoardState b = get(x, y);
                if (b == BoardState.BLOCKED) {
                    System.out.print("* ");
                } else if (b == BoardState.WHITE) {
                    System.out.print("w ");
                } else if (b == BoardState.BLACK) {
                    System.out.print("b ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
    }
    
    public Set<Move> validMoves(PlayerType player) {
        Set<Move> moves = new HashSet<Move>();
        for (int y = 0; y < BOARD_SIZE; y++) {
            for (int x = 0; x < BOARD_SIZE; x++) {
                if (validMove(x, y, player)) {
                    moves.add(new Move(x, y));
                }
            }
        }
        return moves;
    }
    
    private boolean validMove(int x, int y, PlayerType player) {
        // Check if any pieces are flipped
        return makeMove(x, y, player, false) > 0;
    }
    
    private boolean validLocation(int x, int y) {
        return ((x >= 0) && (x < BOARD_SIZE) && (y >= 0) && (y < BOARD_SIZE) &&
                (get(x, y) != BoardState.BLOCKED)); 
        
    }
    
    public int scoreMove(Move m, PlayerType player) {
        return makeMove(m.x, m.y, player, false);
    }
    
    private int makeMove(int x, int y, PlayerType player, boolean commit) {
        if (!validLocation(x, y) || get(x, y) != BoardState.EMPTY) {
            return 0;
        }
        int numFlipped = 0;
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if ((dx == 0) && (dy == 0)) {
                    continue;
                }
                numFlipped += flipPieces(x, y, dx, dy, player, commit);
            }
        }
        if (commit) {
            this.movesPlayed++;
        }
        return numFlipped;
    }
    
    private int flipPieces(int x, int y, int dx, int dy, PlayerType player,
            boolean commit) {
        BoardState current = BoardState.asBoardState(player);
        BoardState opponent = BoardState.asBoardState(player.getOpponent());
        
        x += dx;
        y += dy;
        int opponentPieces = 0;
        while (validLocation(x, y) && get(x, y) == opponent) {
            ++opponentPieces;
            x += dx;
            y += dy;
        }
        if (!validLocation(x, y) || opponentPieces == 0 || get(x, y) != current) {
            // Hit the edge of the board, or a blocked square
            return 0;
        }
        
        if (commit) {
            for (int i = 0; i <= opponentPieces+1; i++) {
                set(x, y, current);
                x -= dx;
                y -= dy;
            }
            subtractCellCount(opponent, opponentPieces);
            addCellCount(current, opponentPieces+1);
        }
        
        return opponentPieces;
    }
    
    private int getCellCount(BoardState b) {
        if (b == BoardState.BLOCKED) {
            return BLOCKED_NUM;
        } else if (b == BoardState.EMPTY) {
            return BOARD_SIZE*BOARD_SIZE - BLOCKED_NUM - movesPlayed;
        } else {
            return cellCount.get(b);
        }
    }
    
    private BoardState getState(byte[] boardArray, int x, int y) {
        return BoardState.fromByte(boardArray[y*BOARD_SIZE + x]);
    }
    
    /**
     * Returns the board state corresponding to the coordinates (x,y).
     * 
     * Because it was becoming confusing as to the implementation details
     * of the board.
     * 
     * @param x
     * @param y
     * @return boardState
     */
    public BoardState get(int x, int y) {
        return this.board[y][x];
    }
    
    /**
     * Set a tile on the board.
     * 
     * Not externally visible.
     * @param x
     * @param y
     * @param b value to set
     */
    private void set(int x, int y, BoardState b) {
        this.board[y][x] = b;
    }
    
    // FIXME this was dependent upon player, but values seemed to be
    // inverted
    public int scoreBoardObjectively() {
        int score = (getCellCount(BoardState.WHITE) -
                     getCellCount(BoardState.BLACK));
        // Preference victories
        if (movesRemaining() == 0) {
            if (score > 0) {
                score += BOARD_SIZE*BOARD_SIZE + 1;
            } else {
                score -= BOARD_SIZE*BOARD_SIZE + 1;
            }
        }
        return score;
    }
    
    public int scoreBoard(PlayerType player) {
        if (player == PlayerType.WHITE) {
            return scoreBoardObjectively();
        } else {
            return -scoreBoardObjectively();
        }
    }
    
    public Board apply(Move m, PlayerType player, boolean destructive) {
        if (destructive) {
            this.makeMove(m.x, m.y, player, true);
            // Does this make sense/do what I think it does?
            return this;
        } else {
            Board result = new Board(this);
            result.makeMove(m.x, m.y, player, true);
            return result;
        }
    }
    
    public int movesRemaining() {
        return BOARD_SIZE*BOARD_SIZE - BLOCKED_NUM - this.movesPlayed;
    }
    
}
