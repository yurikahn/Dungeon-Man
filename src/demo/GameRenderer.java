package demo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import demo.DungeonRoom.DoorEntry;

public class GameRenderer {
    
    /**
     * Efficient storage of colors by ID
     * in an easily expandable and
     * editable form. Allows one to get
     * the correct color to render by
     * giving the universal ID of an object.
     * 
     * Populated by initColors()
     */
    private HashMap<Integer, Color> colorById;
    public static final int PIXEL_SIZE_BLOCK = 50; /* 50 */
    public static final int PIXEL_SIZE_MAPROOM = 8;
    
    public static final Color COLOR_BLUE = new Color(50, 50, 210);
    public static final Color COLOR_GREEN = new Color(50, 210, 50);
    public static final Color COLOR_RED = new Color(210, 50, 50);
    public static final Color COLOR_CYAN = new Color(50, 210, 210);
    public static final Color COLOR_YELLOW = new Color(210, 210, 50);
    public static final Color COLOR_MAGENTA = new Color(210, 50, 210);
    
    public GameRenderer() {
	colorById = new HashMap<Integer, Color>();
	this.init();
    }
    
    /**
     * Init method that populates the
     * color hashmap manually with colors
     * for every ID, from entities to
     * blocks to special thingies.
     */
    private void init() {
	/* Manually add each color */
	colorById.put(GameObject.ID_WALL_BLANK, new Color(220, 220, 230));
	colorById.put(GameObject.ID_WALL_STONE, new Color(40, 40, 130));
	colorById.put(GameObject.ID_BLOCK_BLANK, new Color(220, 220, 230));
	colorById.put(GameObject.ID_BLOCK_STONE, new Color(80, 80, 210));
	colorById.put(GameObject.ID_ENTITY_PLAYER, new Color(200, 60, 30));
	
	loadSprites();
    }
    
    private Image blue;
    private Image green;
    private Image red;
    private Image cyan;
    private Image yellow;
    private Image magenta;
    private Image backdrop;
    
    private void loadSprites() {
	blue = null;
	try {
	    blue = ImageIO.read(new File("blue.png"));
	} catch (IOException e) {}
	green = null;
	try {
	    green = ImageIO.read(new File("green.png"));
	} catch (IOException e) {}
	magenta = null;
	try {
	    magenta = ImageIO.read(new File("magenta.png"));
	} catch (IOException e) {}
	red = null;
	try {
	    red = ImageIO.read(new File("red.png"));
	} catch (IOException e) {}
	cyan = null;
	try {
	    cyan = ImageIO.read(new File("cyan.png"));
	} catch (IOException e) {}
	yellow = null;
	try {
	    yellow = ImageIO.read(new File("yellow.png"));
	} catch (IOException e) {}
	backdrop = null;
	try {
	    backdrop = ImageIO.read(new File("backdrop.png"));
	} catch (IOException e) {}
    }
    
    public void render(Graphics2D g2d, EntityPlayer thePlayer, DungeonRoom[] dungeon, boolean inPlay, int currentRoomIndex,
	    boolean mapHack) {
	if (inPlay) {
	    DungeonRoom currentRoom = dungeon[currentRoomIndex];
	    int pixelSizeX = GameRenderer.PIXEL_SIZE_BLOCK * DungeonRoom.DEFAULT_ROOM_X;
	    int pixelSizeY = GameRenderer.PIXEL_SIZE_BLOCK * DungeonRoom.DEFAULT_ROOM_Y;
	    
	    int realPixelSizeX = GameRenderer.PIXEL_SIZE_BLOCK * currentRoom.getRoomX();
	    int realPixelSizeY = GameRenderer.PIXEL_SIZE_BLOCK * currentRoom.getRoomY();
	    
	    int xOffset = 0;
	    int yOffset = 0;
	    
	    int pixelPlayerX = (int) ((thePlayer.getBounds().getX() + thePlayer.getBounds().getWidth()) * GameRenderer.PIXEL_SIZE_BLOCK);
	    int pixelPlayerY = (int) ((thePlayer.getBounds().getY() + thePlayer.getBounds().getHeight()) * GameRenderer.PIXEL_SIZE_BLOCK);
	    
	    if (pixelPlayerX > pixelSizeX / 2) {
		/*
		 * Right far enough to move camera
		 */
		if (pixelPlayerX < realPixelSizeX - pixelSizeX / 2) {
		    /*
		     * Not far right enough to hit the side
		     */
		    xOffset = pixelPlayerX - pixelSizeX / 2;
		} else {
		    xOffset = realPixelSizeX - pixelSizeX;
		}
	    }
	    
	    if (pixelPlayerY > pixelSizeY / 2) {
		/* Down far enough to move camera */
		if (pixelPlayerY < realPixelSizeY - pixelSizeY / 2) {
		    /*
		     * Not far down enough to hit the bottom
		     */
		    yOffset = pixelPlayerY - pixelSizeY / 2;
		} else {
		    yOffset = realPixelSizeY - pixelSizeY;
		}
	    }
	    
	    ArrayList<Block> currentRoomWalls = currentRoom.getRoomMap();
	    ArrayList<Wall> currentRoomBackdrop = currentRoom.getRoomBackdrop();
	    /* Draw Backdrop */
	    for (int i = 0; i < currentRoomBackdrop.size(); i++) {
		if (backdrop == null) {
		    g2d.setColor(colorById.get(currentRoomBackdrop.get(i).getID()));
		    g2d.fillRect((int) (currentRoomBackdrop.get(i).getBounds().getX() * GameRenderer.PIXEL_SIZE_BLOCK)
			    - xOffset, (int) (currentRoomBackdrop.get(i).getBounds().getY() * GameRenderer.PIXEL_SIZE_BLOCK)
			    - yOffset,
			    (int) (currentRoomBackdrop.get(i).getBounds().getWidth() * GameRenderer.PIXEL_SIZE_BLOCK),
			    (int) (currentRoomBackdrop.get(i).getBounds().getHeight() * GameRenderer.PIXEL_SIZE_BLOCK));
		} else {
		    for (double x = 0; x < currentRoomBackdrop.get(i).getBounds().getWidth(); x++) {
			for (double y = 0; y < currentRoomBackdrop.get(i).getBounds().getHeight(); y++) {
			    g2d.drawImage(backdrop,
				    (int) ((currentRoomBackdrop.get(i).getBounds().getX() + x) * GameRenderer.PIXEL_SIZE_BLOCK)
					    - xOffset,
				    (int) ((currentRoomBackdrop.get(i).getBounds().getY() + y) * GameRenderer.PIXEL_SIZE_BLOCK)
					    - yOffset, (GameRenderer.PIXEL_SIZE_BLOCK), (GameRenderer.PIXEL_SIZE_BLOCK), null);
			}
		    }
		}
	    }
	    
	    /* Draw Walls */
	    for (int i = 0; i < currentRoomWalls.size(); i++) {
		// g2d.setColor(colorById.get(currentRoomWalls.get(i).getID()));
		
		Image image = null;
		if (COLOR_BLUE.equals(currentRoom.biome)) {
		    image = blue;
		}
		if (COLOR_RED.equals(currentRoom.biome)) {
		    image = red;
		}
		if (COLOR_GREEN.equals(currentRoom.biome)) {
		    image = green;
		}
		if (COLOR_CYAN.equals(currentRoom.biome)) {
		    image = cyan;
		}
		if (COLOR_MAGENTA.equals(currentRoom.biome)) {
		    image = magenta;
		}
		if (COLOR_YELLOW.equals(currentRoom.biome)) {
		    image = yellow;
		}
		if (image == null) {
		    g2d.setColor(currentRoom.biome);
		    g2d.fillRect((int) (currentRoomWalls.get(i).getBounds().getX() * GameRenderer.PIXEL_SIZE_BLOCK) - xOffset,
			    (int) (currentRoomWalls.get(i).getBounds().getY() * GameRenderer.PIXEL_SIZE_BLOCK) - yOffset,
			    (int) (currentRoomWalls.get(i).getBounds().getWidth() * GameRenderer.PIXEL_SIZE_BLOCK),
			    (int) (currentRoomWalls.get(i).getBounds().getHeight() * GameRenderer.PIXEL_SIZE_BLOCK));
		} else {
		    for (double x = 0; x < currentRoomWalls.get(i).getBounds().getWidth(); x++) {
			for (double y = 0; y < currentRoomWalls.get(i).getBounds().getHeight(); y++) {
			    g2d.drawImage(image,
				    (int) ((currentRoomWalls.get(i).getBounds().getX() + x) * GameRenderer.PIXEL_SIZE_BLOCK)
					    - xOffset,
				    (int) ((currentRoomWalls.get(i).getBounds().getY() + y) * GameRenderer.PIXEL_SIZE_BLOCK)
					    - yOffset, (GameRenderer.PIXEL_SIZE_BLOCK), (GameRenderer.PIXEL_SIZE_BLOCK), null);
			}
		    }
		}
	    }
	    
	    /* Draw Player */
	    g2d.setColor(colorById.get(GameObject.ID_ENTITY_PLAYER));
	    g2d.fillRect((int) (thePlayer.getBounds().getX() * GameRenderer.PIXEL_SIZE_BLOCK) - xOffset, (int) (thePlayer
		    .getBounds().getY() * GameRenderer.PIXEL_SIZE_BLOCK) - yOffset,
		    (int) (GameRenderer.PIXEL_SIZE_BLOCK * (thePlayer.getBounds().getWidth())),
		    (int) (GameRenderer.PIXEL_SIZE_BLOCK * (thePlayer.getBounds().getHeight())));
	} else {
	    for (int i = 0; i < dungeon.length; i++) {
		if (dungeon[i].numVisit > 0 && !dungeon[i].isVisited() && !mapHack) {
		    g2d.setColor(new Color(53, 53, 53)); // 53
		} else {
		    g2d.setColor(dungeon[i].biome);
		}
		if (!dungeon[i].visitable) {
		    continue;
		}
		if (!dungeon[i].isVisited() && dungeon[i].numVisit == 0 && !mapHack) {
		    continue;
		}
		
		int x = dungeon[i].getRoomSizeX();
		int y = dungeon[i].getRoomSizeY();
		int loc_x = dungeon[i].roomLocX;
		int loc_y = dungeon[i].roomLocY;
		if (true) {// dungeon[i].isVisited()) {
		    g2d.fillRect(loc_x * PIXEL_SIZE_MAPROOM + PIXEL_SIZE_MAPROOM / 8, loc_y * PIXEL_SIZE_MAPROOM
			    + PIXEL_SIZE_MAPROOM / 8, x * PIXEL_SIZE_MAPROOM - PIXEL_SIZE_MAPROOM / 4, y * PIXEL_SIZE_MAPROOM
			    - PIXEL_SIZE_MAPROOM / 4);
		    
		    DungeonRoom.DoorEntry[] doors = dungeon[i].surroundingRooms;
		    for (int j = 0; j < doors.length; j++) {
			if (doors[j].open) {
			    DoorEntry door = dungeon[i].surroundingRooms[j];
			    switch (door.dir) {
				case 2:
				    g2d.fillRect(loc_x * PIXEL_SIZE_MAPROOM + door.index * PIXEL_SIZE_MAPROOM
					    + PIXEL_SIZE_MAPROOM * 3 / 8, loc_y * PIXEL_SIZE_MAPROOM + y * PIXEL_SIZE_MAPROOM
					    - PIXEL_SIZE_MAPROOM / 8, PIXEL_SIZE_MAPROOM / 4, PIXEL_SIZE_MAPROOM / 8);
				    break;
				case 0:
				    g2d.fillRect(loc_x * PIXEL_SIZE_MAPROOM + door.index * PIXEL_SIZE_MAPROOM
					    + PIXEL_SIZE_MAPROOM * 3 / 8, loc_y * PIXEL_SIZE_MAPROOM, PIXEL_SIZE_MAPROOM / 4,
					    PIXEL_SIZE_MAPROOM / 8);
				    break;
				case 1:
				    g2d.fillRect(loc_x * PIXEL_SIZE_MAPROOM + x * PIXEL_SIZE_MAPROOM - PIXEL_SIZE_MAPROOM / 8,
					    loc_y * PIXEL_SIZE_MAPROOM + door.index * PIXEL_SIZE_MAPROOM + PIXEL_SIZE_MAPROOM
						    * 3 / 8, PIXEL_SIZE_MAPROOM / 8, PIXEL_SIZE_MAPROOM / 4);
				    break;
				case 3:
				    g2d.fillRect(loc_x * PIXEL_SIZE_MAPROOM, loc_y * PIXEL_SIZE_MAPROOM + door.index
					    * PIXEL_SIZE_MAPROOM + PIXEL_SIZE_MAPROOM * 3 / 8, PIXEL_SIZE_MAPROOM / 8,
					    PIXEL_SIZE_MAPROOM / 4);
				    break;
			    }
			}
		    }
		}
	    }
	    int x = dungeon[currentRoomIndex].getRoomSizeX();
	    int y = dungeon[currentRoomIndex].getRoomSizeY();
	    int loc_x = dungeon[currentRoomIndex].roomLocX;
	    int loc_y = dungeon[currentRoomIndex].roomLocY;
	    
	    g2d.setColor(new Color(250, 250, 250));
	    g2d.fillRect(loc_x * PIXEL_SIZE_MAPROOM + PIXEL_SIZE_MAPROOM / 4, loc_y * PIXEL_SIZE_MAPROOM + PIXEL_SIZE_MAPROOM
		    / 4, x * PIXEL_SIZE_MAPROOM - PIXEL_SIZE_MAPROOM / 2, y * PIXEL_SIZE_MAPROOM - PIXEL_SIZE_MAPROOM / 2);
	}
    }
}
