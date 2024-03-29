package demo;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import javax.imageio.ImageIO;

import engine.Bounds;

public class DungeonGenerator {
    
    private class ArrayRoomEntry {
	
	public int xSize;
	public int ySize;
	public int index;
	public int x;
	public int y;
	
	public ArrayRoomEntry(int xSize, int ySize, int index, int x, int y) {
	    this.xSize = xSize;
	    this.ySize = ySize;
	    this.index = index;
	    this.x = x;
	    this.y = y;
	}
    }
    
    private class LayoutEntry {
	
	public boolean[] doors;
	public Image     image;
	
	public LayoutEntry(Image image) {
	    BufferedImage tile = (BufferedImage) image;
	    this.image = tile.getSubimage(0, 1, DungeonRoom.DEFAULT_ROOM_X, DungeonRoom.DEFAULT_ROOM_Y);
	    int x = 0;
	    ArrayList<Boolean> doorList = new ArrayList<Boolean>();
	    while (true) {
		int RGBCode = tile.getRGB(x, 0);
		int R = RGBCode & 0x00FF0000 / (256 * 256);
		int G = RGBCode & 0x0000FF00 / 256;
		int B = RGBCode & 0x000000FF;
		if (R == 0 && G == 0 && B == 0) {
		    doorList.add(true);
		}
		if (R == 255 && G == 255 && B == 255) {
		    doorList.add(false);
		}
		if (R == 180 && G == 180 && B == 180) {
		    break;
		}
		x++;
	    }
	    this.doors = new boolean[doorList.size()];
	    for (int i = 0; i < doorList.size(); i++) {
		doors[i] = doorList.get(i);
	    }
	}
    }
    
    private class BFSNode {
	
	public BFSNode(DungeonRoom room, int depth) {
	    this.room = room;
	    this.depth = depth;
	}
	
	public DungeonRoom room;
	public int	 depth;
    }
    
    private DungeonRoom[]	  dungeonMap;
    
    private ArrayRoomEntry[]       roomArray;
    
    private int		    dungeonX;
    private int		    dungeonY;
    
    private int		    numberOfRooms;
    
    private ArrayList<LayoutEntry> layoutList;
    
    public DungeonGenerator(int xSize, int ySize) {
	this.dungeonX = xSize;
	this.dungeonY = ySize;
    }
    
    public static int wrap(int i, int limit) {
	if (i < 0) { return i + limit; }
	if (i >= limit) { return i - limit; }
	return i;
    }
    
    public static double wrap(double i, int limit) {
	if (i < 0) { return i + limit; }
	if (i >= limit) { return i - limit; }
	return i;
    }
    
    private void loadLayouts() {
	BufferedImage layouts = null;
	try {
	    layouts = ImageIO.read(new File("assets/levels/1-1.png"));
	} catch (IOException e) {}
	if (layouts == null) { return; }
	layoutList = new ArrayList<LayoutEntry>();
	int tileHeight = DungeonRoom.DEFAULT_ROOM_Y + 1;
	for (int y = 0; y < layouts.getHeight(null); y += tileHeight) {
	    layoutList.add(new LayoutEntry(layouts.getSubimage(0, y, DungeonRoom.DEFAULT_ROOM_X, tileHeight)));
	}
    }
    
    /* Populate grid (step 1) */
    private int[][] generatePseudoMap() {
	ArrayList<ArrayRoomEntry> roomEntryList = new ArrayList<ArrayRoomEntry>();
	
	int[][] preMap = new int[dungeonX][dungeonY];
	for (int x = 0; x < dungeonX; x++) {
	    for (int y = 0; y < dungeonY; y++) {
		preMap[x][y] = -1;
	    }
	}
	int roomIndex = 0;
	for (int x = 0; x < dungeonX; x++) {
	    for (int y = 0; y < dungeonY; y++) {
		if (preMap[x][y] != -1) {
		    continue;
		}
		while (true) {
		    int xSize = 1;
		    int ySize = 1;
		    
		    int chance = 3; /* 1/value, default=6 */
		    if (GameMain.RANDOM.nextInt(chance) == 0) {
			xSize += 1;
			if (GameMain.RANDOM.nextInt(chance) == 0) {
			    xSize += 1;
			}
		    }
		    if (GameMain.RANDOM.nextInt(chance) == 0) {
			ySize += 1;
			if (GameMain.RANDOM.nextInt(chance) == 0) {
			    ySize += 1;
			}
		    }
		    if (checkForBlanks(preMap, -1, x, y, xSize, ySize)) {
			if (xSize == 1 && ySize == 1) {
			    break;
			}
			continue;
		    } else {
			roomEntryList.add(new ArrayRoomEntry(xSize, ySize, roomIndex, x, y));
			for (int rx = 0; rx < xSize; rx++) {
			    for (int ry = 0; ry < ySize; ry++) {
				preMap[x + rx][y + ry] = roomIndex;
			    }
			}
			break;
		    }
		}
		roomIndex++;
	    }
	}
	
	roomArray = roomEntryList.toArray(new ArrayRoomEntry[roomEntryList.size()]);
	numberOfRooms = roomIndex;
	return preMap;
    }
    
    private boolean checkForBlanks(int[][] map, int blank, int x, int y, int xs, int ys) {
	if (x + xs - 1 >= dungeonX) { return true; }
	if (y + ys - 1 >= dungeonY) { return true; }
	
	for (int xi = x; xi < x + xs; xi++) {
	    for (int yi = y; yi < y + ys; yi++) {
		if (map[xi][yi] != blank) { return true; }
	    }
	}
	return false;
    }
    
    /* Generate the node map (step 2) */
    private DungeonRoom[] generateNodeMap(int[][] pseudoMap) {
	
	DungeonRoom[] roomList = new DungeonRoom[numberOfRooms];
	for (int i = 0; i < roomList.length; i++) {
	    roomList[i] = new DungeonRoom(i, roomArray[i].xSize, roomArray[i].ySize, roomArray[i].x, roomArray[i].y);
	}
	for (int x = 0; x < dungeonX; x++) {
	    for (int y = 0; y < dungeonY; y++) {
		int roomIndex = pseudoMap[x][y];
		int roomIndexUp = pseudoMap[x][wrap(y - 1, dungeonY)];
		int roomIndexDown = pseudoMap[x][wrap(y + 1, dungeonY)];
		int roomIndexLeft = pseudoMap[wrap(x - 1, dungeonX)][y];
		int roomIndexRight = pseudoMap[wrap(x + 1, dungeonX)][y];
		if (roomIndexUp != roomIndex) {
		    roomList[roomIndex].addDoor(roomIndexUp, Game.DIR_UP);
		}
		if (roomIndexDown != roomIndex) {
		    roomList[roomIndex].addDoor(roomIndexDown, Game.DIR_DOWN);
		}
		if (roomIndexLeft != roomIndex) {
		    roomList[roomIndex].addDoor(roomIndexLeft, Game.DIR_LEFT);
		}
		if (roomIndexRight != roomIndex) {
		    roomList[roomIndex].addDoor(roomIndexRight, Game.DIR_RIGHT);
		}
	    }
	}
	for (int i = 0; i < roomList.length; i++) {
	    roomList[i].solidifyArray();
	}
	return roomList;
    }
    
    private Bounds getRoomBounds(int i) {
	DungeonRoom room = dungeonMap[i];
	return new Bounds(dungeonMap[i].roomLocX, dungeonMap[i].roomLocY, dungeonMap[i].getRoomSizeX(), dungeonMap[i].getRoomSizeY());
    }
    
    private double sq(double d) {
	return d * d;
    }
    
    /* Choose which doors to use (step 3) */
    private void cullNodeMap() {
	int doorChance = 4; /* 1/val default=2 */
	for (int i = 0; i < dungeonMap.length; i++) {
	    DungeonRoom.DoorEntry[] possibleDoors = dungeonMap[i].surroundingRooms;
	    
	    Bounds place = getRoomBounds(i);
	    doorChance = Math.abs(Math.min(
		    Math.min(
			    Math.min(Math.min((int) Math.sqrt(sq(place.x - (dungeonX / 2)) + sq(place.y - (dungeonY / 2))), (int) Math.sqrt(sq(place.x - (dungeonX / 2 + dungeonX / 4)) + sq(place.y - (dungeonY / 2)))),
				    (int) Math.sqrt(sq(place.x - (dungeonX / 2)) + sq(place.y - (dungeonY / 2 + dungeonY / 4)))), (int) Math.sqrt(sq(place.x - (dungeonX / 2 - dungeonX / 4)) + sq(place.y - (dungeonY / 2)))),
		    (int) Math.sqrt(sq(place.x - (dungeonX / 2)) + sq(place.y - (dungeonY / 2 - dungeonY / 4))))) / 4 + 1; /*
															    * /
															    * 4
															    */
	    if (GameMain.RANDOM.nextInt(doorChance) == 0) {
		possibleDoors = openRandomDoor(possibleDoors, i);
	    }
	    
	    for (int d = 0; d < possibleDoors.length / 4; d++) {
		if (GameMain.RANDOM.nextInt(doorChance) == 0) {
		    possibleDoors = openRandomDoor(possibleDoors, i);
		    if (GameMain.RANDOM.nextInt(doorChance) == 0) {
			possibleDoors = openRandomDoor(possibleDoors, i);
		    }
		}
	    }
	    dungeonMap[i].surroundingRooms = possibleDoors;
	}
	for (int i = 0; i < dungeonMap.length; i++) {
	    dungeonMap[i].fillDoors(dungeonMap);
	}
    }
    
    public DungeonRoom.DoorEntry[] openRandomDoor(DungeonRoom.DoorEntry[] possible, int currentRoom) {
	int currentDoorIndex = GameMain.RANDOM.nextInt(possible.length);
	DungeonRoom.DoorEntry doorToOpen = possible[currentDoorIndex];
	doorToOpen.open = true;
	int counterDoor = DungeonRoom.getCounterDoor(dungeonMap, Game.DUNGEON_SIZE_X, Game.DUNGEON_SIZE_Y, currentRoom, currentDoorIndex)[1];
	dungeonMap[doorToOpen.destination].surroundingRooms[counterDoor].open = true;
	return possible;
    }
    
    public int getIndex(int x, int y) {
	for (int i = 0; i < dungeonMap.length; i++) {
	    if (x >= dungeonMap[i].roomLocX && x <= dungeonMap[i].roomLocX + dungeonMap[i].getRoomSizeX() && y >= dungeonMap[i].roomLocY && y <= dungeonMap[i].roomLocY + dungeonMap[i].getRoomSizeY()) { return i; }
	}
	return -1;
    }
    
    /* Iterative queued BFS solution to graph traverse */
    private void visitVisitableBFS(int index) {
	Queue<BFSNode> queue = new LinkedList<BFSNode>();
	queue.add(new BFSNode(dungeonMap[index], 0));
	dungeonMap[index].depth = 0;
	dungeonMap[index].visit();
	
	while (!queue.isEmpty()) {
	    BFSNode node = queue.remove();
	    for (int i = 0; i < node.room.surroundingRooms.length; i++) {
		BFSNode child = new BFSNode(dungeonMap[node.room.surroundingRooms[i].destination], node.depth + 1);
		if (dungeonMap[node.room.surroundingRooms[i].destination].depth > node.depth + 1) {
		    dungeonMap[node.room.surroundingRooms[i].destination].depth = node.depth + 1;
		}
		if (child.room.isVisited() || !node.room.surroundingRooms[i].open) {
		    continue;
		}
		child.room.visit();
		queue.add(child);
	    }
	}
    }
    
    /* Recusive DFS solution to the graph traverse */
    private void visitVisitableDFS(int depth, int index) {
	if (dungeonMap[index].depth > depth) {
	    dungeonMap[index].depth = depth;
	}
	if (dungeonMap[index].numVisit > dungeonMap[index].surroundingRooms.length) {
	    return;
	} else {
	    // dungeonMap[index].visit();
	    dungeonMap[index].visitable = true;
	    dungeonMap[index].numVisit++;
	}
	for (int i = 0; i < dungeonMap[index].surroundingRooms.length; i++) {
	    if (dungeonMap[index].surroundingRooms[i].open) {
		visitVisitableDFS(depth + 1, dungeonMap[index].surroundingRooms[i].destination);
	    }
	}
    }
    
    private void generateBiomes() {
	double R = 70; // 70
	double G = 100; // 100
	double B = 230; // 230
	
	boolean METHOD_HUE = false;
	boolean METHOD_BIOME = true;
	boolean partition = true;
	for (int i = 0; i < dungeonMap.length; i++) {
	    dungeonMap[i].numVisit = 0;
	    Color color = new Color(0, 0, 0);
	    double r = 0, g = 0, b = 0;
	    if (METHOD_HUE) {
		// Second method: HSL, where depth determines hue
		float depth = dungeonMap[i].depth / 5; // /5 = DFS *9 = BFS
		color = Color.getHSBColor(((depth + 180) % 360) / 360, 0.5f, 0.75f); // 0.5f
										     // 0.75f
		R = color.getRed();
		G = color.getGreen();
		B = color.getBlue();
	    }
	    if (METHOD_BIOME) {
		// First method: RGB Randomization of depth, interesting band
		// pattern
		int depth = dungeonMap[i].depth;
		if (depth == 0) {
		    depth = 10;
		}
		double FACTOR = 1.1; // 2.2 // 1.1
		r = ((R / 255.0) * (depth)) * FACTOR % 255;
		g = ((G / 255.0) * (depth)) * FACTOR % 255;
		b = ((B / 255.0) * (depth)) * FACTOR % 255;
	    }
	    if (partition) {
		// Partition by color
		int high = 210;
		int low = 50;
		
		// sparse border = 7
		// common border = 13
		// new zones = 50
		int buffer = 50;
		if (Math.max(Math.max(r, g), b) == r) {
		    r = high;
		    if (Math.abs(r - g) < buffer) { // && g>b) {
			g = high;
			b = low;
		    } else {
			g = low;
			if (Math.abs(r - b) < buffer) { // && b>g) {
			    b = high;
			} else {
			    b = low;
			}
		    }
		}
		if (Math.max(Math.max(r, g), b) == g) {
		    g = high;
		    if (Math.abs(g - r) < buffer) { // && r>b) {
			r = high;
			b = low;
		    } else {
			r = low;
			if (Math.abs(g - b) < buffer) { // && b>r) {
			    b = high;
			} else {
			    b = low;
			}
		    }
		}
		if (Math.max(Math.max(r, g), b) == b) {
		    b = high;
		    if (Math.abs(b - r) < buffer) { // && r>g) {
			r = high;
			g = low;
		    } else {
			r = low;
			if (Math.abs(b - g) < buffer) { // && g>r) {
			    g = high;
			} else {
			    g = low;
			}
		    }
		}
		color = new Color((int) r, (int) g, (int) b);
		dungeonMap[i].biome = color;
	    }
	}
    }
    
    private void decorateMap() {
	for (int i = 0; i < dungeonMap.length; i++) {
	    Image relate = null;
	    if (dungeonMap[i].getRoomSizeX() == 1 && dungeonMap[i].getRoomSizeY() == 1) {
		for (int j = 0; j < layoutList.size(); j++) {
		    boolean fail = false;
		    for (int k = 0; k < dungeonMap[i].surroundingRooms.length; k++) {
			if (dungeonMap[i].surroundingRooms[k].open != layoutList.get(j).doors[dungeonMap[i].surroundingRooms[k].dir]) {
			    fail = true;
			}
		    }
		    if (!fail) {
			relate = layoutList.get(j).image;
		    }
		}
	    }
	    dungeonMap[i].fillImage(relate);
	}
    }
    
    /**
     * Creates the map and randomly generates rooms.
     */
    public void generate() {
	
	/*
	 * Dungeon generation: Grid-based multisize node graph with edge culling
	 * proportional to distance from generator nodes
	 */
	loadLayouts();
	dungeonMap = generateNodeMap(generatePseudoMap());
	cullNodeMap();
	decorateMap();
	
	/*
	 * Biome generation: Posturized colorizing using RGB cycler of DFS depth
	 * from player spawn with inter-room smoothing
	 */
	visitVisitableDFS(0, getIndex(dungeonX / 2, dungeonY / 2));
	generateBiomes();
    }
    
    public DungeonRoom[] getDungeon() {
	return dungeonMap;
    }
}
