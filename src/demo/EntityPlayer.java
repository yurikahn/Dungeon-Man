package demo;

public class EntityPlayer extends Entity {
    
    public static final double XSIZE = 1.796875; // 1.8
    public static final double YSIZE = 2.0; // 2.2
    
    public EntityPlayer(Bounds bounds) {
	super(Entity.ID_ENTITY_PLAYER, bounds);
    }
}
