package demo;

public class GameObject {

	public static final int ID_BLOCK_BLANK = 0;
	public static final int ID_BLOCK_STONE = 1;
	
	public static final int ID_WALL_BLANK = 0;
	public static final int ID_WALL_STONE = 1;
	
	public static final int ID_ENTITY_PLAYER = 6;
	
	public static final int ID_TRIGGER_TELEPORT = 0;
	
	private int ID;
	private Bounds bounds;
	
	public GameObject(int ID, Bounds bounds) {
		this.ID = ID;
		this.bounds = bounds;
	}
	
	public int getID() {
		return ID;
	}
	
	public Bounds getBounds() {
		return bounds;
	}
	
	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}
}
