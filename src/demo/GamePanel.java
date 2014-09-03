package demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JPanel;

/**
 * The game user interface, handles IO, and passes commands off to the other
 * parts of the game.
 * 
 * @author Moore
 */
public class GamePanel extends JPanel {
    
    /**
     * Version UID, because they say so.
     */
    private static final long    serialVersionUID = 8978416916942029249L;
    
    /**
     * In pixels, the dimensions of the panel.
     */
    private int		  panelXSize;
    private int		  panelYSize;
    
    /**
     * In pixels, the side length of each block to render (game unit).
     */
    private int		  blockSize;
    
    /**
     * Keep the x and y size of the visible board to render, to make things a
     * little simpler.
     */
    private int		  boardX;
    private int		  boardY;
    
    /**
     * The game instance does all the grunt work of the game processing and
     * stores the full data of the world.
     */
    private Game		 gameInstance;
    
    /**
     * This is a way of making the input less like a text field and more like a
     * game - it should help smooth out movement and make the controls less
     * sticky.
     */
    private ArrayList<Character> keyList;
    private ArrayList<Character> keyLock;
    
    /**
     * Current position of the mouse.
     */
    private int		  mouseX;
    private int		  mouseY;
    
    public GamePanel(int xDimension, int yDimension) {
	this.panelXSize = xDimension;
	this.panelYSize = yDimension;
	this.blockSize = GameRenderer.PIXEL_SIZE_BLOCK; /* Standard = 40 */
	this.mouseX = 0;
	this.mouseY = 0;
	this.boardX = panelXSize / blockSize;
	this.boardY = panelYSize / blockSize;
	
	/* Init game */
	gameInstance = new Game(this, boardX, boardY);
	
	/* Resize window */
	this.setPreferredSize(new Dimension(panelXSize, panelYSize));
	
	/* Set up input system */
	keyList = new ArrayList<Character>();
	keyLock = new ArrayList<Character>();
	this.addKeyListener(new KeyAdapter() {
	    
	    @Override
	    public void keyPressed(KeyEvent event) {
		pressKey(event.getKeyChar());
	    }
	    
	    @Override
	    public void keyReleased(KeyEvent event) {
		releaseKey(event.getKeyChar());
	    }
	});
	
	this.addMouseMotionListener(new MouseMotionListener() {
	    
	    @Override
	    public void mouseMoved(MouseEvent event) {
		mouseX = event.getX();
		mouseY = event.getY();
	    }
	    
	    @Override
	    public void mouseDragged(MouseEvent arg0) {}
	});
	this.setFocusable(true);
    }
    
    /**
     * Adds a virtual input.
     * 
     * @param key
     */
    public void pressKey(char key) {
	if (keyLock.contains(key)) { return; }
	if (!keyList.contains(key)) {
	    keyList.add(key);
	}
    }
    
    /**
     * Removes a virtual input.
     * 
     * @param key
     */
    public void releaseKey(char key) {
	if (keyList.contains(key)) {
	    keyList.remove((Character) key);
	}
	if (keyLock.contains(key)) {
	    keyLock.remove((Character) key);
	}
    }
    
    public void lockKey(char key) {
	releaseKey(key);
	if (!keyLock.contains(key)) {
	    keyLock.add(key);
	}
    }
    
    /**
     * Called repeatedly to step through the game. It sends and receives I/O
     * data from the game instance.
     */
    public void step() {/* Send keyboard input in a game-like fashion */
	gameInstance.addInput(keyList, mouseX, mouseY);
	
	/* Advance the game */
	gameInstance.advance();
	
	/* Render to the user */
	this.repaint();
    }
    
    /**
     * JPanel's paint method, modified - this is the main rendering section of
     * the game. It draws basic graphics for all visible entities and for the
     * game world using Graphics2D.
     */
    @Override
    public void paint(Graphics basicGraphics) {
	Graphics2D g2d = (Graphics2D) basicGraphics;
	
	/* Render a background to prevent windows-failiure type effects. */
	g2d.setColor(new Color(50, 50, 50));
	g2d.fillRect(0, 0, panelXSize * 3, panelYSize * 3);
	
	gameInstance.draw(g2d);
    }
}
