package engine;

import java.awt.geom.Rectangle2D;

@SuppressWarnings("serial")
public class Bounds extends Rectangle2D.Double {
    
    public Bounds(double x, double y, double w, double h) {
	super(x, y, w, h);
    }
}
