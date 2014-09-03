package demo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import demo.DungeonRoom.DoorEntry;
import engine.Block;
import engine.Bounds;
import engine.Wall;

public class GameRenderer {
    
    public static final int   PIXEL_SIZE_BLOCK   = 64;		     /* 50 */
    public static final int   PIXEL_SIZE_MAPROOM = 8;
    
    public static final Color COLOR_BLUE	 = new Color(50, 50, 210);
    public static final Color COLOR_GREEN	= new Color(50, 210, 50);
    public static final Color COLOR_RED	  = new Color(210, 50, 50);
    public static final Color COLOR_CYAN	 = new Color(50, 210, 210);
    public static final Color COLOR_YELLOW       = new Color(210, 210, 50);
    public static final Color COLOR_MAGENTA      = new Color(210, 50, 210);
    
    private long	      steps	      = 0;
    
    public GameRenderer() {
	this.init();
    }
    
    /**
     * Init method that populates the color hashmap manually with colors for
     * every ID, from entities to blocks to special thingies.
     */
    private void init() {
	loadSprites();
    }
    
    private Image   blue;
    private Image   green;
    private Image   red;
    private Image   cyan;
    private Image   yellow;
    private Image   magenta;
    private Image   backdrop;
    private Image[] player;
    
    private void loadSprites() {
	blue = null;
	try {
	    blue = ImageIO.read(new File("assets/blue.png"));
	} catch (IOException e) {}
	green = null;
	try {
	    green = ImageIO.read(new File("assets/green.png"));
	} catch (IOException e) {}
	magenta = null;
	try {
	    magenta = ImageIO.read(new File("assets/magenta.png"));
	} catch (IOException e) {}
	red = null;
	try {
	    red = ImageIO.read(new File("assets/red.png"));
	} catch (IOException e) {}
	cyan = null;
	try {
	    cyan = ImageIO.read(new File("assets/cyan.png"));
	} catch (IOException e) {}
	yellow = null;
	try {
	    yellow = ImageIO.read(new File("assets/yellow.png"));
	} catch (IOException e) {}
	backdrop = null;
	try {
	    backdrop = ImageIO.read(new File("assets/backdrop.png"));
	} catch (IOException e) {}
	
	player = new Image[65];
	Image playerSheet = null;
	int playerHeight = 64;
	int playerWidth = 64;
	try {
	    playerSheet = ImageIO.read(new File("assets/player2.png"));
	} catch (IOException e) {}
	for (int i = 0; i < player.length; i++) {
	    int hoff = i / 8;
	    System.out.println(i + ", " + hoff);
	    player[i] = ((BufferedImage) playerSheet).getSubimage((i % 8) * playerWidth, playerHeight * hoff, playerWidth, playerHeight);
	}
    }
    
    public void render(Graphics2D g2d, EntityPlayer thePlayer, DungeonRoom[] dungeon, boolean inPlay, int currentRoomIndex, boolean mapHack) {
	if (inPlay) {
	    steps++;
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
		    g2d.setColor(new Color(50, 50, 50));
		    g2d.fillRect((int) (currentRoomBackdrop.get(i).getBounds().getX() * GameRenderer.PIXEL_SIZE_BLOCK) - xOffset, (int) (currentRoomBackdrop.get(i).getBounds().getY() * GameRenderer.PIXEL_SIZE_BLOCK) - yOffset, (int) (currentRoomBackdrop
			    .get(i).getBounds().getWidth() * GameRenderer.PIXEL_SIZE_BLOCK), (int) (currentRoomBackdrop.get(i).getBounds().getHeight() * GameRenderer.PIXEL_SIZE_BLOCK));
		} else {
		    for (double x = 0; x < currentRoomBackdrop.get(i).getBounds().getWidth(); x++) {
			for (double y = 0; y < currentRoomBackdrop.get(i).getBounds().getHeight(); y++) {
			    g2d.drawImage(backdrop, (int) ((currentRoomBackdrop.get(i).getBounds().getX() + x) * GameRenderer.PIXEL_SIZE_BLOCK) - xOffset * 1 / ((int) currentRoomBackdrop.get(i).getLevel() + 1), (int) ((currentRoomBackdrop.get(i)
				    .getBounds().getY() + y) * GameRenderer.PIXEL_SIZE_BLOCK)
				    - yOffset * 1 / ((int) currentRoomBackdrop.get(i).getLevel() + 1), (GameRenderer.PIXEL_SIZE_BLOCK), (GameRenderer.PIXEL_SIZE_BLOCK), null);
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
		if (image == null || currentRoomWalls.get(i) instanceof BlockPlatform) {
		    g2d.setColor(currentRoom.biome);
		    g2d.fillRect((int) (currentRoomWalls.get(i).getBounds().getX() * GameRenderer.PIXEL_SIZE_BLOCK) - xOffset, (int) (currentRoomWalls.get(i).getBounds().getY() * GameRenderer.PIXEL_SIZE_BLOCK) - yOffset, (int) (currentRoomWalls.get(i)
			    .getBounds().getWidth() * GameRenderer.PIXEL_SIZE_BLOCK), (int) (currentRoomWalls.get(i).getBounds().getHeight() * GameRenderer.PIXEL_SIZE_BLOCK));
		} else {
		    for (double x = 0; x < currentRoomWalls.get(i).getBounds().getWidth(); x++) {
			for (double y = 0; y < currentRoomWalls.get(i).getBounds().getHeight(); y++) {
			    g2d.drawImage(image, (int) ((currentRoomWalls.get(i).getBounds().getX() + x) * GameRenderer.PIXEL_SIZE_BLOCK) - xOffset, (int) ((currentRoomWalls.get(i).getBounds().getY() + y) * GameRenderer.PIXEL_SIZE_BLOCK) - yOffset,
				    (GameRenderer.PIXEL_SIZE_BLOCK), (GameRenderer.PIXEL_SIZE_BLOCK), null);
			}
		    }
		}
	    }
	    
	    /* Draw Player */
	    /*
	     * g2d.setColor(COLOR_RED); g2d.fillRect((int)
	     * (thePlayer.getBounds().getX() * GameRenderer.PIXEL_SIZE_BLOCK) -
	     * xOffset, (int) (thePlayer.getBounds().getY() *
	     * GameRenderer.PIXEL_SIZE_BLOCK) - yOffset, (int)
	     * (GameRenderer.PIXEL_SIZE_BLOCK *
	     * (thePlayer.getBounds().getWidth())), (int)
	     * (GameRenderer.PIXEL_SIZE_BLOCK *
	     * (thePlayer.getBounds().getHeight())));
	     * g2d.setColor(COLOR_YELLOW); if (thePlayer.getFacing() ==
	     * Game.DIR_LEFT) { g2d.fillRect((int)
	     * ((thePlayer.getBounds().getX() + thePlayer.getBounds().getWidth()
	     * - 0.2) * GameRenderer.PIXEL_SIZE_BLOCK) - xOffset, (int)
	     * (thePlayer.getBounds().getY() * GameRenderer.PIXEL_SIZE_BLOCK) -
	     * yOffset, (int) (GameRenderer.PIXEL_SIZE_BLOCK * 0.2), (int)
	     * (GameRenderer.PIXEL_SIZE_BLOCK *
	     * (thePlayer.getBounds().getHeight()))); } else {
	     * g2d.fillRect((int) (thePlayer.getBounds().getX() *
	     * GameRenderer.PIXEL_SIZE_BLOCK) - xOffset, (int)
	     * (thePlayer.getBounds().getY() * GameRenderer.PIXEL_SIZE_BLOCK) -
	     * yOffset, (int) (GameRenderer.PIXEL_SIZE_BLOCK * 0.2), (int)
	     * (GameRenderer.PIXEL_SIZE_BLOCK *
	     * (thePlayer.getBounds().getHeight()))); }
	     */
	    Bounds b = thePlayer.getBounds();
	    Image imageToRender = player[64];
	    int renderX = (int) (b.x * GameRenderer.PIXEL_SIZE_BLOCK) - xOffset;
	    int renderY = (int) (b.y * GameRenderer.PIXEL_SIZE_BLOCK) - yOffset;
	    int renderWidth = (int) (b.width * GameRenderer.PIXEL_SIZE_BLOCK);
	    int renderHeight = (int) (b.height * GameRenderer.PIXEL_SIZE_BLOCK);
	    if (thePlayer.getOnGround() && thePlayer.getSpeedX() == 0) {
		imageToRender = player[64];
	    } else if (!thePlayer.getOnGround()) {
		imageToRender = player[45];
	    } else {
		imageToRender = player[(int) (((steps) % (8 * 5)) / 5) + 4];
	    }
	    if (thePlayer.getFacing() == Game.DIR_RIGHT) {
		imageToRender = getFlippedImage((BufferedImage) (imageToRender));
	    }
	    
	    g2d.drawImage(imageToRender, renderX, renderY, renderWidth, renderHeight, null);
	    
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
		    g2d.fillRect(loc_x * PIXEL_SIZE_MAPROOM + PIXEL_SIZE_MAPROOM / 8, loc_y * PIXEL_SIZE_MAPROOM + PIXEL_SIZE_MAPROOM / 8, x * PIXEL_SIZE_MAPROOM - PIXEL_SIZE_MAPROOM / 4, y * PIXEL_SIZE_MAPROOM - PIXEL_SIZE_MAPROOM / 4);
		    
		    DungeonRoom.DoorEntry[] doors = dungeon[i].surroundingRooms;
		    for (int j = 0; j < doors.length; j++) {
			if (doors[j].open) {
			    DoorEntry door = dungeon[i].surroundingRooms[j];
			    switch (door.dir) {
				case 2:
				    g2d.fillRect(loc_x * PIXEL_SIZE_MAPROOM + door.index * PIXEL_SIZE_MAPROOM + PIXEL_SIZE_MAPROOM * 3 / 8, loc_y * PIXEL_SIZE_MAPROOM + y * PIXEL_SIZE_MAPROOM - PIXEL_SIZE_MAPROOM / 8, PIXEL_SIZE_MAPROOM / 4,
					    PIXEL_SIZE_MAPROOM / 8);
				    break;
				case 0:
				    g2d.fillRect(loc_x * PIXEL_SIZE_MAPROOM + door.index * PIXEL_SIZE_MAPROOM + PIXEL_SIZE_MAPROOM * 3 / 8, loc_y * PIXEL_SIZE_MAPROOM, PIXEL_SIZE_MAPROOM / 4, PIXEL_SIZE_MAPROOM / 8);
				    break;
				case 1:
				    g2d.fillRect(loc_x * PIXEL_SIZE_MAPROOM + x * PIXEL_SIZE_MAPROOM - PIXEL_SIZE_MAPROOM / 8, loc_y * PIXEL_SIZE_MAPROOM + door.index * PIXEL_SIZE_MAPROOM + PIXEL_SIZE_MAPROOM * 3 / 8, PIXEL_SIZE_MAPROOM / 8,
					    PIXEL_SIZE_MAPROOM / 4);
				    break;
				case 3:
				    g2d.fillRect(loc_x * PIXEL_SIZE_MAPROOM, loc_y * PIXEL_SIZE_MAPROOM + door.index * PIXEL_SIZE_MAPROOM + PIXEL_SIZE_MAPROOM * 3 / 8, PIXEL_SIZE_MAPROOM / 8, PIXEL_SIZE_MAPROOM / 4);
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
	    g2d.fillRect(loc_x * PIXEL_SIZE_MAPROOM + PIXEL_SIZE_MAPROOM / 4, loc_y * PIXEL_SIZE_MAPROOM + PIXEL_SIZE_MAPROOM / 4, x * PIXEL_SIZE_MAPROOM - PIXEL_SIZE_MAPROOM / 2, y * PIXEL_SIZE_MAPROOM - PIXEL_SIZE_MAPROOM / 2);
	}
    }
    
    public static BufferedImage getFlippedImage(BufferedImage bi) {
	BufferedImage flipped = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
	AffineTransform tran = AffineTransform.getTranslateInstance(bi.getWidth(), 0);
	AffineTransform flip = AffineTransform.getScaleInstance(-1d, 1d);
	tran.concatenate(flip);
	
	Graphics2D g = flipped.createGraphics();
	g.setTransform(tran);
	g.drawImage(bi, 0, 0, null);
	g.dispose();
	
	return flipped;
    }
}
