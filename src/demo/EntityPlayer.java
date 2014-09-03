package demo;

import engine.Bounds;
import engine.Entity;

public class EntityPlayer extends Entity {
    
    public static final double XSIZE = 1.796875; // 1.8
    public static final double YSIZE = 3.0;     // 2.2
						 
    public EntityPlayer(Bounds bounds) {
	super(bounds, 0, 0, 0.1, 0.1, 0.05, 0.05, 0.01, 0.25, 0.3);
    }
}
