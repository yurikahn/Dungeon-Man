package demo;

import engine.Bounds;
import engine.Entity;

public class EntityPlayer extends Entity {
    
    public static final double XSIZE = 1.1; // 1.8
    public static final double YSIZE = 3;  // 2.2
					    
    public EntityPlayer(Bounds bounds) {
	super(bounds, 0, 0, 0.2, 0.2, 0.05, 0.05, 0.03, 0.4, 0.5);
    }
}
