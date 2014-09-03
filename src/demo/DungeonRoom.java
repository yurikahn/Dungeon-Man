package demo;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import engine.Block;
import engine.Bounds;
import engine.Trigger;
import engine.Wall;

public class DungeonRoom {
    
    public class DoorEntry {
	
	public DoorEntry(int destination, int index, int dir, int outLocX, int outLocY, boolean open) {
	    this.destination = destination;
	    this.index = index;
	    this.dir = dir;
	    this.open = open;
	    this.outLocX = outLocX;
	    this.outLocY = outLocY;
	}
	
	public int     destination;
	public int     index;
	public int     dir;
	public boolean open;
	public int     outLocX;
	public int     outLocY;
    }
    
    public ArrayList<Block>      roomMap;
    public ArrayList<Wall>       roomBackdrop;
    
    private ArrayList<Trigger>   triggerList;
    
    private int		  roomX;
    private int		  roomY;
    
    public int		   roomLocX;
    public int		   roomLocY;
    
    private int		  roomSizeX;
    private int		  roomSizeY;
    
    private boolean	      visited	= false;
    public boolean	       visitable      = false;
    
    public static final int      DEFAULT_ROOM_X = 16;
    public static final int      DEFAULT_ROOM_Y = 12;
    
    public static final int      DOOR_SIZE      = 4;
    
    private ArrayList<DoorEntry> surrounding;
    public DoorEntry[]	   surroundingRooms;
    
    private int		  numDoorsTop    = 0;
    private int		  numDoorsBottom = 0;
    private int		  numDoorsLeft   = 0;
    private int		  numDoorsRight  = 0;
    
    private int		  roomIndex;
    
    public int		   depth;
    public int		   numVisit;
    
    public Color		 biome;
    
    public DungeonRoom(int roomIndex, int roomX, int roomY, int locX, int locY) {
	this.roomLocX = locX;
	this.roomLocY = locY;
	
	this.roomIndex = roomIndex;
	
	this.roomX = roomX * DEFAULT_ROOM_X;
	this.roomY = roomY * DEFAULT_ROOM_Y;
	
	this.roomSizeX = roomX;
	this.roomSizeY = roomY;
	
	this.init();
	surrounding = new ArrayList<DoorEntry>();
	triggerList = new ArrayList<Trigger>();
	depth = 999999;
	biome = new Color(0, 0, 0);
    }
    
    public void solidifyArray() {
	surroundingRooms = surrounding.toArray(new DoorEntry[surrounding.size()]);
    }
    
    public void addDoor(int doorRef, int dir) {
	int index = 0;
	
	int outLocX = 0;
	int outLocY = 0;
	switch (dir) {
	    case Game.DIR_UP:
		index = numDoorsTop;
		numDoorsTop++;
		outLocX = index * DEFAULT_ROOM_X + DEFAULT_ROOM_X / 2 - DOOR_SIZE / 2;
		break;
	    case Game.DIR_RIGHT:
		index = numDoorsRight;
		numDoorsRight++;
		outLocX = this.roomX - 1;
		outLocY = index * DEFAULT_ROOM_Y + DEFAULT_ROOM_Y / 2 - DOOR_SIZE / 2;
		break;
	    case Game.DIR_DOWN:
		index = numDoorsBottom;
		numDoorsBottom++;
		outLocX = index * DEFAULT_ROOM_X + DEFAULT_ROOM_X / 2 - DOOR_SIZE / 2;
		outLocY = this.roomY - 1;
		break;
	    case Game.DIR_LEFT:
		index = numDoorsLeft;
		numDoorsLeft++;
		outLocY = index * DEFAULT_ROOM_Y + DEFAULT_ROOM_Y / 2 - DOOR_SIZE / 2;
		break;
	    default:
		return;
	}
	DoorEntry entry = new DoorEntry(doorRef, index, dir, outLocX, outLocY, false);
	surrounding.add(entry);
    }
    
    public int getRoomX() {
	return roomX;
    }
    
    public int getRoomY() {
	return roomY;
    }
    
    public int getRoomSizeX() {
	return roomSizeX;
    }
    
    public int getRoomSizeY() {
	return roomSizeY;
    }
    
    public void visit() {
	visited = true;
    }
    
    public boolean isVisited() {
	return visited;
    }
    
    public ArrayList<Trigger> getTriggerList() {
	return triggerList;
    }
    
    public void init() {
	roomMap = new ArrayList<Block>();
	roomBackdrop = new ArrayList<Wall>();
	
	roomBackdrop.add(new WallStone(new Bounds(0, 0, roomX, roomY)));
	
    }
    
    public void fillImage(Image input) {
	double doorSize = DOOR_SIZE;
	
	if (roomSizeX == 1 && roomSizeY == 1) {
	    int open = 0;
	    for (int i = 0; i < this.surroundingRooms.length; i++) {
		if (surroundingRooms[i].open) {
		    open++;
		}
	    }
	    if (open == 4) {
		try {
		    input = ImageIO.read(new File("assets/levels/1.png"));
		} catch (IOException e) {}
	    }
	}
	
	double bottom = roomY - 1;
	double right = roomX - 1;
	if (input == null) {
	    for (int x = 0; x < roomSizeX; x++) {
		double roomCX = x * DEFAULT_ROOM_X;
		double halfRoom = DEFAULT_ROOM_X / 2;
		roomMap.add(new BlockStone(new Bounds(roomCX, 0, halfRoom - doorSize / 2, 1)));
		roomMap.add(new BlockStone(new Bounds(roomCX + halfRoom + doorSize / 2, 0, halfRoom - doorSize / 2, 1)));
		
		roomMap.add(new BlockStone(new Bounds(roomCX, bottom, halfRoom - doorSize / 2, 1)));
		roomMap.add(new BlockStone(new Bounds(roomCX + halfRoom + doorSize / 2, bottom, halfRoom - doorSize / 2, 1)));
		
		roomMap.add(new BlockStone(new Bounds(DEFAULT_ROOM_X / 2 - DOOR_SIZE / 2 + x * DEFAULT_ROOM_X, 5, DOOR_SIZE, 1)));
	    }
	    for (int y = 0; y < roomSizeY; y++) {
		double roomCY = y * DEFAULT_ROOM_Y;
		double halfRoom = DEFAULT_ROOM_Y / 2;
		roomMap.add(new BlockStone(new Bounds(0, roomCY, 1, halfRoom - doorSize / 2)));
		roomMap.add(new BlockStone(new Bounds(0, roomCY + halfRoom + doorSize / 2, 1, halfRoom - doorSize / 2)));
		
		roomMap.add(new BlockStone(new Bounds(right, roomCY, 1, halfRoom - doorSize / 2)));
		roomMap.add(new BlockStone(new Bounds(right, roomCY + halfRoom + doorSize / 2, 1, halfRoom - doorSize / 2)));
	    }
	} else {
	    for (int x = 0; x < roomX; x++) {
		for (int y = 0; y < roomY; y++) {
		    int R = (((BufferedImage) input).getRGB(x, y) & 0x00FF0000) / (256 * 256);
		    int G = (((BufferedImage) input).getRGB(x, y) & 0x0000FF00) / 256;
		    int B = ((BufferedImage) input).getRGB(x, y) & 0x000000FF;
		    if (R == 0 && G == 0 && B == 0) {
			roomMap.add(new BlockStone(new Bounds(x, y, 1, 1)));
		    }
		    if (R == 0 && G == 0 && B == 255) {
			roomMap.add(new BlockPlatform(new Bounds(x, y, 1, 0.5)));
		    }
		}
	    }
	}
    }
    
    public void fillDoors(DungeonRoom[] dungeon) {
	for (int i = 0; i < this.surroundingRooms.length; i++) {
	    DoorEntry door = surroundingRooms[i];
	    int xOffset = 0;
	    int yOffset = 0;
	    
	    if (!door.open) {
		switch (door.dir) {
		    case Game.DIR_DOWN:
			yOffset = roomY - 1;
		    case Game.DIR_UP:
			roomMap.add(new BlockStone(new Bounds(DEFAULT_ROOM_X / 2 - DOOR_SIZE / 2 + door.index * DEFAULT_ROOM_X, yOffset, DOOR_SIZE, 1)));
			break;
		    case Game.DIR_RIGHT:
			xOffset = roomX - 1;
		    case Game.DIR_LEFT:
			roomMap.add(new BlockStone(new Bounds(xOffset, DEFAULT_ROOM_Y / 2 - DOOR_SIZE / 2 + door.index * DEFAULT_ROOM_Y, 1, DOOR_SIZE)));
			break;
		}
	    } else {
		xOffset = -1;
		yOffset = -1;
		int[] counterDoorCoords = getCounterDoor(dungeon, Game.DUNGEON_SIZE_X, Game.DUNGEON_SIZE_Y, roomIndex, i);
		int destIndex = counterDoorCoords[0];
		
		double destX = dungeon[destIndex].surroundingRooms[counterDoorCoords[1]].outLocX;
		double destY = dungeon[destIndex].surroundingRooms[counterDoorCoords[1]].outLocY;
		switch (door.dir) {
		    case Game.DIR_DOWN:
			yOffset = roomY;
			destY += EntityPlayer.YSIZE / 2;
		    case Game.DIR_UP:
			destY -= EntityPlayer.YSIZE / 2;
			
			triggerList.add(new TriggerTeleport(new Bounds(DEFAULT_ROOM_X / 2 - DOOR_SIZE / 2 + door.index * DEFAULT_ROOM_X, yOffset, DOOR_SIZE, 1), new Bounds(destX, destY, 0, 0), destIndex, false, true, door.dir));
			break;
		    case Game.DIR_RIGHT:
			xOffset = roomX;
			destX += EntityPlayer.XSIZE / 2;
		    case Game.DIR_LEFT:
			destX -= EntityPlayer.XSIZE / 2;
			
			triggerList.add(new TriggerTeleport(new Bounds(xOffset, DEFAULT_ROOM_Y / 2 - DOOR_SIZE / 2 + door.index * DEFAULT_ROOM_Y, 1, DOOR_SIZE), new Bounds(destX, destY, 0, 0), destIndex, true, false, door.dir));
			break;
		}
	    }
	}
    }
    
    public static int[] getRoomByDoor(DungeonRoom[] map, int mapX, int mapY, int roomIndex, DoorEntry door, boolean useDestinationCoords) {
	int[] returnCoords = new int[2];
	returnCoords[0] = map[roomIndex].roomLocX;
	returnCoords[1] = map[roomIndex].roomLocY;
	switch (door.dir) {
	    case Game.DIR_DOWN:
		returnCoords[1] += map[roomIndex].roomSizeY - 1;
	    case Game.DIR_UP:
		returnCoords[0] += door.index;
		break;
	    case Game.DIR_RIGHT:
		returnCoords[0] += map[roomIndex].roomSizeX - 1;
	    case Game.DIR_LEFT:
		returnCoords[1] += door.index;
		break;
	}
	if (useDestinationCoords) {
	    switch (door.dir) {
		case Game.DIR_DOWN:
		    returnCoords[1] += 1;
		    break;
		case Game.DIR_UP:
		    returnCoords[1] -= 1;
		    break;
		case Game.DIR_LEFT:
		    returnCoords[0] -= 1;
		    break;
		case Game.DIR_RIGHT:
		    returnCoords[0] += 1;
		    break;
	    }
	}
	returnCoords[0] = wrap(returnCoords[0], mapX);
	returnCoords[1] = wrap(returnCoords[1], mapY);
	return returnCoords;
    }
    
    public static int wrap(int i, int limit) {
	if (i < 0) { return i + limit; }
	if (i >= limit) { return i - limit; }
	return i;
    }
    
    /* Return format: {roomIndex, doorIndex} */
    public static int[] getCounterDoor(DungeonRoom[] map, int mapX, int mapY, int roomIndex, int doorIndex) {
	int counterRoomIndex = -1;
	int counterDoorIndex = -1;
	DoorEntry door = map[roomIndex].surroundingRooms[doorIndex];
	
	counterRoomIndex = door.destination;
	DoorEntry[] counterDoors = map[counterRoomIndex].surroundingRooms;
	for (int i = 0; i < counterDoors.length; i++) {
	    if (counterDoors[i].destination != roomIndex && counterDoors[i].dir != (door.dir + 2) % 4) {
		continue;
	    }
	    /* Cut this out for basic */
	    int[] counterCoords = getRoomByDoor(map, mapX, mapY, counterRoomIndex, counterDoors[i], true);
	    int[] currentCoords = getRoomByDoor(map, mapX, mapY, roomIndex, door, false);
	    if (counterCoords[0] == currentCoords[0] && counterCoords[1] == currentCoords[1]) {
		/* End cut for basic */
		counterDoorIndex = i;
		int[] ra = new int[2];
		ra[0] = counterRoomIndex;
		ra[1] = counterDoorIndex;
		return ra;
	    }
	}
	return null;
    }
    
    public ArrayList<Block> getRoomMap() {
	return roomMap;
    }
    
    public ArrayList<Wall> getRoomBackdrop() {
	return roomBackdrop;
    }
}
