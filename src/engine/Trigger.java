package engine;

public class Trigger {
    
    private Bounds bounds;
    
    public Trigger(Bounds bounds) {
	this.bounds = bounds;
    }
    
    public Entity trigger(Entity e) {
	return e;
    }
    
    public Bounds getBounds() {
	return bounds;
    }
    
    public void setBounds(Bounds bounds) {
	this.bounds = bounds;
    }
}
