package iago;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

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
    
    private Board board;
    private Container content;
    private BufferedImage offscreen;
    
    // Using the Tango icon theme palette:
    private static final Color BG_COLOUR = new Color(78, 154, 6);
    private static final Color BLOCKED_COLOUR = new Color(237, 212, 0);
    private static final Color WHITE_COLOUR = new Color(238, 238, 236);
    private static final Color BLACK_COLOUR = new Color(46, 52, 54);
    
    public GameScreen() {
        super("Othello");
        
        this.board = new Board();
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
        //blit();
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
                    g.fillRect(x*lengthx, y*lengthy, lengthx, lengthy);
                    break;
                case 'b':
                    g.setColor(BLACK_COLOUR);
                    g.fillRect(x*lengthx, y*lengthy, lengthx, lengthy);
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
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void mouseMoved(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
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
}
