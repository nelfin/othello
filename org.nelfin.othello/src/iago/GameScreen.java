package iago;

import iago.Player.PlayerType;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

public class GameScreen extends JFrame implements MouseListener, MouseMotionListener {
    /**
     * 
     */
    private static final long serialVersionUID = 6079508161832398122L;
    private static final int xdim = 800;
    private static final int ydim = 800;
    private static final int gridx = Board.BOARD_SIZE;
    private static final int gridy = Board.BOARD_SIZE;
    private static final int lengthx = xdim/gridx;
    private static final int lengthy = ydim/gridy;
    private static final long DELAY = 20;
    
    private Board board;
    private PlayerType colour;
    private Set<Point> moveRects;
    private Map<Point, Move> rectLookup;
    private Container content;
    private BufferedImage offscreen;
    private Move chosenMove;
    private boolean moveChosen;
    
    // Using the Tango icon theme palette:
    private static final Color BG_COLOUR = new Color(78, 154, 6);
    private static final Color HIGHLIGHT_COLOUR = new Color(138, 226, 52);
    private static final Color BLOCKED_COLOUR = new Color(237, 212, 0);
    private static final Color WHITE_COLOUR = new Color(238, 238, 236);
    private static final Color BLACK_COLOUR = new Color(46, 52, 54);
    private static final int arcH = 20;
    private static final int arcW = 20;
    
    public GameScreen(PlayerType colour) {
        super("Othello");
        
        this.board = new Board();
        this.colour = colour;
        this.moveRects = new HashSet<Point>();
        this.rectLookup = new HashMap<Point, Move>();
        this.chosenMove = null;
        this.moveChosen = false;
        
        this.content = getContentPane();
        this.offscreen = new BufferedImage(xdim, ydim, BufferedImage.TYPE_INT_RGB);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(xdim, ydim);
        setFocusable(true);
        addMouseMotionListener(this);
        addMouseListener(this);
        this.content.setBackground(BG_COLOUR);
        
        setVisible(true);
    }
    
    public void update(Board b) {
        this.board = b;
        Set<Move> legalMoves = this.board.validMoves(this.colour);
        this.moveRects.clear();
        this.rectLookup.clear();
        for (Move m : legalMoves) {
            Point rect = new Point(m.x*lengthx, m.y*lengthy);
            this.moveRects.add(rect);
            this.rectLookup.put(rect, m);
        }
        draw();
        blit();
    }
    
    public void draw() {
        char[] repr = this.board.toString().toCharArray();
        Graphics g = this.offscreen.getGraphics();
        
        g.setColor(BG_COLOUR);
        g.fillRect(0, 0, xdim, ydim);
        
        for (int y = 0; y < gridy; y++) {
            for (int x = 0; x < gridx; x++) {
                char c = get(repr, gridy, x, y);
                switch (c) {
                case '*':
                    g.setColor(BLOCKED_COLOUR);
                    g.fillRect(x*lengthx, y*lengthy, lengthx, lengthy);
                    break;
                case 'w':
                    g.setColor(WHITE_COLOUR);
                    g.fillRoundRect(x*lengthx, y*lengthy, lengthx, lengthy, arcW, arcH);
                    break;
                case 'b':
                    g.setColor(BLACK_COLOUR);
                    g.fillRoundRect(x*lengthx, y*lengthy, lengthx, lengthy, arcW, arcH);
                    break;
                }
            }
        }
    }
    
    private char get(char[] boardArray, int boardSize, int x, int y) {
        return boardArray[y*boardSize + x];
    }
    
    public void blit() {
        Graphics g = this.content.getGraphics();
        g.drawImage(this.offscreen, 0, 0, null);
    }
    
    @Override
    public void mouseClicked(MouseEvent arg0) {
        Point p = arg0.getPoint();
        for (Point rect : this.moveRects) {
            int dx = p.x - rect.x;
            int dy = p.y - rect.y;
            if (0 <= dx && dx < lengthx && 0 <= dy && dy < lengthy) {
                notifyMove(getMove(rect));
            }
        }
    }
    
    private void notifyMove(Move move) {
        chosenMove= move;
        moveChosen = true;
    }
    
    private Move getMove(Point rect) {
        return rectLookup.get(rect);
    }
    
    @Override
    public void mouseMoved(MouseEvent arg0) {
        Point p = arg0.getPoint();
        for (Point rect : this.moveRects) {
            int dx = p.x - rect.x;
            int dy = p.y - rect.y;
            if (0 <= dx && dx < lengthx && 0 <= dy && dy < lengthy) {
                // above: Manhattan distance -> inside the box
                highlight(rect);
                return;
            }
        }
        draw();
        blit();
    }
    
    private void highlight(Point rect) {
        Graphics g = offscreen.getGraphics();
        g.setColor(HIGHLIGHT_COLOUR);
        g.fillRect(rect.x, rect.y, lengthx, lengthy);
        blit();
    }
    
    @Override
    public void mouseEntered(MouseEvent arg0) {
    }
    
    @Override
    public void mouseExited(MouseEvent arg0) {
    }
    
    @Override
    public void mousePressed(MouseEvent arg0) {
    }
    
    @Override
    public void mouseReleased(MouseEvent arg0) {
    }
    
    @Override
    public void mouseDragged(MouseEvent arg0) {
    }
    
    public Move pollForMove() {
        while (!this.moveChosen) {
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        this.moveChosen = false;
        return chosenMove;
    }
}
