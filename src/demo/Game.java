package demo;

import java.awt.Graphics2D;
import java.util.ArrayList;

import engine.Bounds;
import engine.Trigger;

/**
 * The _game_ part of the actual game. This keeps track of the world and player,
 * and makes sense of the I/O. It does the processing for the output while the
 * panel class does only the rendering.
 * 
 * @author Moore
 */
public class Game {
    
    public static final int DIR_UP	 = 0;
    public static final int DIR_DOWN       = 2;
    public static final int DIR_LEFT       = 3;
    public static final int DIR_RIGHT      = 1;
    
    /**
     * Dimensions of the dungeon TODO: Infinite dungeon
     */
    private int	     dungeonX;
    private int	     dungeonY;
    private DungeonRoom[]   dungeon;
    
    /**
     * The one and only, all holy player object.
     */
    private EntityPlayer    player;
    
    /**
     * For the help of the display, the decimal part of the display.
     */
    public int	      startPreciseX;
    public int	      startPreciseY;
    public int	      currentRoom;
    
    private boolean	 inPlay	 = true;
    public boolean	  map	    = false;
    public boolean	  mapHack	= false;
    public boolean	  playerOnGround = false;
    
    private GameRenderer    renderer;
    private GamePanel       ioPanel;
    
    public static final int DUNGEON_SIZE_X = 64;
    public static final int DUNGEON_SIZE_Y = 64;
    
    public Game(GamePanel ioPanel, int panelX, int panelY) {
	
	this.dungeonX = DUNGEON_SIZE_X;
	this.dungeonY = DUNGEON_SIZE_Y;
	
	player = new EntityPlayer(new Bounds(5.5, 5.5, EntityPlayer.XSIZE, EntityPlayer.YSIZE));
	this.ioPanel = ioPanel;
	currentRoom = 0;
	
	/* Populate color map */
	renderer = new GameRenderer();
	
	DungeonGenerator generator = new DungeonGenerator(dungeonX, dungeonY);
	generator.generate();
	dungeon = generator.getDungeon();
	currentRoom = generator.getIndex(dungeonX / 2, dungeonY / 2);
    }
    
    /**
     * Handle input from player using a custom keyboard input system.
     * 
     * @param inputArray
     */
    
    public void addInput(ArrayList<Character> inputArray, int mouseX, int mouseY) {
	for (int i = 0; i < inputArray.size(); i++) {
	    handleKeyPress(inputArray.get(i), mouseX, mouseY);
	}
    }
    
    /**
     * Handles a single key input
     * 
     * @param key
     */
    public void handleKeyPress(char input, int mouseX, int mouseY) {
	if ('m' == input) { /* MAP SCREEN */
	    inPlay = false;
	    map = true;
	}
	if ('c' == input) {
	    inPlay = false;
	    map = true;
	    mapHack = true;
	}
	
	if (inPlay) {
	    if ('w' == input) {
		player.jump();
		ioPanel.releaseKey('w');
	    }
	    if ('s' == input) {
		player.addSpeedY(player.getAcceleration());
	    }
	    if ('a' == input) {
		player.addSpeedX(-player.getAcceleration());
	    }
	    if ('d' == input) {
		player.addSpeedX(player.getAcceleration());
	    }
	}
    }
    
    /**
     * Step forward all entities
     */
    public void advance() {
	/* Do things */
	
	applyTriggers();
	
	/* +++ MOVEMENT ++++ */
	
	player.stepMovement(dungeon[currentRoom].getRoomMap());
    }
    
    private boolean applyTriggers() {
	ArrayList<Trigger> triggerList = dungeon[currentRoom].getTriggerList();
	boolean found = false;
	for (int i = 0; i < triggerList.size(); i++) {
	    if (triggerList.get(i) instanceof TriggerTeleport) {
		TriggerTeleport tele = (TriggerTeleport) triggerList.get(i);
		if (player.getBounds().intersects(tele.getBounds())) {
		    player = (EntityPlayer) tele.trigger(player);
		    currentRoom = tele.getDestIndex();
		    found = true;
		}
	    }
	}
	dungeon[currentRoom].visit();
	for (int i = 0; i < dungeon[currentRoom].surroundingRooms.length; i++) {
	    if (dungeon[currentRoom].surroundingRooms[i].open) {
		dungeon[dungeon[currentRoom].surroundingRooms[i].destination].numVisit++;
	    }
	}
	return found;
    }
    
    /* ----------- RENDERING SECTION ------------ */
    
    /**
     * Drawing method placed in the Game class for easy scoping.
     */
    public void draw(Graphics2D g2d) {
	renderer.render(g2d, player, dungeon, inPlay, currentRoom, mapHack);
	inPlay = true;
	map = false;
	mapHack = false;
    }
    
}
