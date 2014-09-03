package demo;

import engine.Bounds;
import engine.Entity;
import engine.Trigger;

public class TriggerTeleport extends Trigger {
    
    private Bounds  destination;
    
    private int     destIndex;
    private boolean changeX;
    private boolean changeY;
    public int      dir;
    
    public TriggerTeleport(Bounds bounds, Bounds destination, int destIndex, boolean changeX, boolean changeY, int dir) {
	super(bounds);
	this.destination = destination;
	this.destIndex = destIndex;
	this.changeX = changeX;
	this.changeY = changeY;
	this.dir = dir;
    }
    
    public int getDestIndex() {
	return destIndex;
    }
    
    @Override
    public Entity trigger(Entity e) {
	/*
	 * Resize to fit - since we do this for everything we can ignore the
	 * corruption
	 */
	
	Bounds dst = new Bounds(destination.getX(), destination.getY(), 0, 0);
	double yoff = (e.getBounds().getY() - this.getBounds().getY());
	if (!changeX) {
	    dst.x = (e.getBounds().getX() - this.getBounds().getX()) + destination.getX();
	}
	if (!changeY) {
	    dst.y = yoff + destination.getY();
	}
	dst.height = e.getBounds().height;
	dst.width = e.getBounds().width;
	e.setBounds(dst);
	return e;
    }
}
