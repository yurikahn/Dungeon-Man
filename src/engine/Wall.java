package engine;

public class Wall implements Comparable<Wall> {
    
    private Bounds bounds;
    private double paralaxLevel;
    
    public Wall(Bounds bounds, double paralaxLevel) {
	this.bounds = bounds;
	this.paralaxLevel = paralaxLevel;
    }
    
    public Bounds getBounds() {
	return bounds;
    }
    
    public void setBounds(Bounds bounds) {
	this.bounds = bounds;
    }
    
    public double getLevel() {
	return paralaxLevel;
    }
    
    public void setLevel(double level) {
	paralaxLevel = level;
    }
    
    @Override
    public int compareTo(Wall w) {
	if (w.getLevel() > this.paralaxLevel) { return 1; }
	if (w.getLevel() < this.paralaxLevel) { return -1; }
	return 0;
    }
}
