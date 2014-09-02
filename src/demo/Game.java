package demo;

import java.awt.Graphics2D;
import java.util.ArrayList;

/**
 * The _game_ part of the actual game.
 * This keeps track of the world and
 * player, and makes sense of the I/O.
 * It does the processing for the
 * output while the panel class does
 * only the rendering.
 * 
 * @author Moore
 *
 */
public class Game {
    
    public static final int DIR_UP = 0;
    public static final int DIR_DOWN = 2;
    public static final int DIR_LEFT = 3;
    public static final int DIR_RIGHT = 1;
    /**
     * Dimensions of the dungeon
     * TODO: Infinite dungeon
     */
    private int dungeonX;
    private int dungeonY;
    
    private DungeonRoom[] dungeon;
    
    /**
     * The one and only, all holy
     * player object.
     */
    private EntityPlayer thePlayer;
    
    /**
     * For the help of the display, the decimal part
     * of the display.
     */
    public int startPreciseX;
    public int startPreciseY;
    
    public int currentRoom;
    
    private double currentRequestedChangeY;
    private double currentRequestedChangeX;
    
    private double playerSpeedX = 0.0;
    private double playerSpeedY = 0.0;
    private boolean speedXChangeThisTick = false;
    private boolean speedYChangeThisTick = false;
    
    private boolean inPlay = true;
    public boolean map = false;
    public boolean mapHack = false;
    public boolean playerOnGround = false;
    
    private GameRenderer renderer;
    
    private GamePanel ioPanel;
    
    public static final int DUNGEON_SIZE_X = 64;
    public static final int DUNGEON_SIZE_Y = 64;
    
    public Game(GamePanel ioPanel, int panelX, int panelY) {
	
	this.dungeonX = DUNGEON_SIZE_X;
	this.dungeonY = DUNGEON_SIZE_Y;
	
	thePlayer = new EntityPlayer(new Bounds(5.5, 5.5, EntityPlayer.XSIZE, EntityPlayer.YSIZE));
	this.ioPanel = ioPanel;
	currentRoom = 0;
	
	this.currentRequestedChangeX = 0.0;
	this.currentRequestedChangeY = 0.0;
	
	/* Populate color map */
	renderer = new GameRenderer();
	
	DungeonGenerator generator = new DungeonGenerator(dungeonX, dungeonY);
	generator.generate();
	dungeon = generator.getDungeon();
	currentRoom = generator.getIndex(dungeonX / 2, dungeonY / 2);
    }
    
    /**
     * Handle input from player using a
     * custom keyboard input system.
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
	double maxSpeed = 0.2;
	double speedStep = 0.1;
	
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
	    if ('w' == input) { /* UP */
		playerSpeedY = -0.6;
		ioPanel.releaseKey('w');
		/*
		 * playerSpeedY -= speedStep;
		 * if (playerSpeedY < -maxSpeed) {
		 * playerSpeedY = -maxSpeed;
		 * }
		 */
		speedYChangeThisTick = true;
	    }
	    if ('a' == input) { /* LEFT */
		playerSpeedX -= speedStep;
		if (playerSpeedX < -maxSpeed) {
		    playerSpeedX = -maxSpeed;
		}
		speedXChangeThisTick = true;
	    }
	    if ('s' == input) { /* DOWN */
		playerSpeedY += speedStep;
		if (playerSpeedY > maxSpeed) {
		    playerSpeedY = maxSpeed;
		}
		speedYChangeThisTick = true;
	    }
	    if ('d' == input) { /* RIGHT */
		playerSpeedX += speedStep;
		if (playerSpeedX > maxSpeed) {
		    playerSpeedX = maxSpeed;
		}
		speedXChangeThisTick = true;
	    }
	}
    }
    
    public static int wrap(int i, int limit) {
	if (i < 0) { return i + limit; }
	if (i >= limit) { return i - limit; }
	return i;
    }
    
    private Block[] intersectWithMap(Bounds entityRect, ArrayList<Block> map) {
	ArrayList<Block> intersect = new ArrayList<Block>();
	
	for (int i = 0; i < map.size(); i++) {
	    if (map.get(i).getID() != GameObject.ID_BLOCK_BLANK) {
		Bounds blockRect = map.get(i).getBounds();
		if (entityRect.intersects(blockRect)) {
		    intersect.add(map.get(i));
		}
	    }
	}
	if (intersect.size() == 0) { return null; }
	return intersect.toArray(new Block[intersect.size()]);
    }
    
    private double handleHMovement(Block[] thingsToCheck, double goalX, double rx) {
	for (int i = 0; i < thingsToCheck.length; i++) {
	    double rightBound = thingsToCheck[i].getBounds().getX() + thingsToCheck[i].getBounds().getWidth();
	    double leftBound = thingsToCheck[i].getBounds().getX();
	    if (rx > 0) {
		/* --- RIGHT --- */
		if (rightBound > goalX) {
		    goalX = leftBound - thePlayer.getBounds().getWidth();
		    playerSpeedX = 0;
		}
	    }
	    if (rx < 0) {
		/* --- LEFT --- */
		if (goalX < rightBound) {
		    goalX = rightBound;
		    playerSpeedX = 0;
		}
	    }
	}
	return goalX;
    }
    
    private double handleVMovement(Block[] thingsToCheck, double goalY, double ry) {
	for (int i = 0; i < thingsToCheck.length; i++) {
	    double downBound = thingsToCheck[i].getBounds().getY() + thingsToCheck[i].getBounds().getHeight();
	    double upBound = thingsToCheck[i].getBounds().getY();
	    if (ry > 0) {
		/* --- DOWN --- */
		if (downBound > goalY) {
		    goalY = upBound - thePlayer.getBounds().getHeight();
		    playerSpeedY = 0;
		}
	    }
	    if (ry < 0) {
		/* --- UP --- */
		if (goalY < downBound) {
		    goalY = downBound;
		    playerSpeedY = 0;
		}
	    }
	}
	return goalY;
    }
    
    /**
     * Step forward all entities
     */
    public void advance() {
	/* Do things */
	
	applyTriggers();
	
	/* +++ MOVEMENT ++++ */
	/* For the sake of my wrists */
	if (playerSpeedY > 0.3) {
	    playerSpeedY = 0.3;
	}
	this.currentRequestedChangeX += playerSpeedX;
	this.currentRequestedChangeY += playerSpeedY;
	double rx = currentRequestedChangeX;
	double ry = currentRequestedChangeY;
	double px = thePlayer.getBounds().getX();
	double py = thePlayer.getBounds().getY();
	
	/* Desired location */
	double goalX = rx + px;
	double goalY = ry + py;
	
	double finalPX = goalX;
	double finalPY = goalY;
	
	/* Don't bother if there's no problem */
	Block[] thingsToCheck = null;
	if ((thingsToCheck = intersectWithMap(new Bounds(goalX, py, EntityPlayer.XSIZE, EntityPlayer.YSIZE),
		dungeon[currentRoom].getRoomMap())) != null) {
	    finalPX = handleHMovement(thingsToCheck, goalX, rx);
	}
	
	if ((thingsToCheck = intersectWithMap(new Bounds(finalPX, goalY, EntityPlayer.XSIZE, EntityPlayer.YSIZE),
		dungeon[currentRoom].getRoomMap())) != null) {
	    finalPY = handleVMovement(thingsToCheck, goalY, ry);
	}
	
	thePlayer.setBounds(new Bounds(finalPX, finalPY, EntityPlayer.XSIZE, EntityPlayer.YSIZE));
	
	/* Reset changes in movement */
	currentRequestedChangeX = 0.0;
	currentRequestedChangeY = 0.0;
	
	deceleratePlayer(0.1);
	
    }
    
    private boolean applyTriggers() {
	ArrayList<Trigger> triggerList = dungeon[currentRoom].getTriggerList();
	boolean found = false;
	for (int i = 0; i < triggerList.size(); i++) {
	    if (triggerList.get(i) instanceof TriggerTeleport) {
		TriggerTeleport tele = (TriggerTeleport) triggerList.get(i);
		if (thePlayer.getBounds().intersects(tele.getBounds())) {
		    thePlayer = (EntityPlayer) tele.trigger(thePlayer);
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
    
    private void deceleratePlayer(double friction) {
	if (!speedXChangeThisTick) {
	    if (Math.abs(playerSpeedX) < friction) {
		playerSpeedX = 0;
	    }
	    if (playerSpeedX > 0) {
		playerSpeedX -= friction;
	    } else if (playerSpeedX < 0) {
		playerSpeedX += friction;
	    }
	}
	if (!speedYChangeThisTick) {
	    playerSpeedY += 0.05;
	    /*
	     * if (Math.abs(playerSpeedY) < friction) {
	     * playerSpeedY = 0;
	     * }
	     * if (playerSpeedY > 0) {
	     * playerSpeedY -= friction;
	     * } else if (playerSpeedY < 0) {
	     * playerSpeedY += friction;
	     * }
	     */
	}
	speedYChangeThisTick = false;
	speedXChangeThisTick = false;
    }
    
    /* ----------- RENDERING SECTION ------------ */
    
    /**
     * Drawing method placed in the Game class for easy scoping.
     */
    public void draw(Graphics2D g2d) {
	renderer.render(g2d, thePlayer, dungeon, inPlay, currentRoom, mapHack);
	inPlay = true;
	map = false;
	mapHack = false;
    }
    
}
