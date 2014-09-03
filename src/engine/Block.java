package engine;

public class Block {
    
    private Bounds  bounds;
    private boolean solid;
    
    public Block(Bounds bounds, boolean solid) {
	this.bounds = bounds;
	this.solid = solid;
    }
    
    public Bounds getBounds() {
	return bounds;
    }
    
    public void setBounds(Bounds bounds) {
	this.bounds = bounds;
    }
    
    public boolean isSolid() {
	return solid;
    }
    
    public void setSolid(boolean solid) {
	this.solid = solid;
    }
}
