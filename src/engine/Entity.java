package engine;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import demo.BlockPlatform;
import demo.EntityPlayer;
import demo.Game;

public class Entity {
    
    private Bounds  bounds;
    
    private double  speedX;
    private double  speedY;
    private double  maxSpeedX;
    private double  maxSpeedY;
    private double  friction;
    private double  acceleration;
    private double  gravity;
    private double  jumpForce;
    private double  maxFallSpeed;
    
    private int     facing;
    
    private boolean moveThisTickX;
    private boolean moveThisTickY;
    
    private long    steps;
    
    public Entity(Bounds bounds, double speedX, double speedY, double maxSpeedX, double maxSpeedY, double friction, double acceleration, double gravity, double maxFallSpeed, double jumpForce) {
	super();
	this.bounds = bounds;
	this.speedX = speedX;
	this.speedY = speedY;
	this.maxSpeedX = maxSpeedX;
	this.maxSpeedY = maxSpeedY;
	this.friction = friction;
	this.acceleration = acceleration;
	this.gravity = gravity;
	this.maxFallSpeed = maxFallSpeed;
	this.jumpForce = jumpForce;
	
	this.moveThisTickX = false;
	this.moveThisTickY = false;
	
	this.facing = Game.DIR_LEFT;
    }
    
    public Bounds getBounds() {
	return bounds;
    }
    
    public void setBounds(Bounds bounds) {
	this.bounds = bounds;
    }
    
    public double getSpeedX() {
	return speedX;
    }
    
    public void setSpeedX(double speedX) {
	this.speedX = speedX;
    }
    
    public double getSpeedY() {
	return speedY;
    }
    
    public void setSpeedY(double speedY) {
	this.speedY = speedY;
    }
    
    public double getMaxSpeedX() {
	return maxSpeedX;
    }
    
    public void setMaxSpeedX(double maxSpeedX) {
	this.maxSpeedX = maxSpeedX;
    }
    
    public double getMaxSpeedY() {
	return maxSpeedY;
    }
    
    public void setMaxSpeedY(double maxSpeedY) {
	this.maxSpeedY = maxSpeedY;
    }
    
    public double getFriction() {
	return friction;
    }
    
    public void setFriction(double friction) {
	this.friction = friction;
    }
    
    public double getAcceleration() {
	return acceleration;
    }
    
    public void setAcceleration(double acceleration) {
	this.acceleration = acceleration;
    }
    
    public double getGravity() {
	return gravity;
    }
    
    public void setGravity(double gravity) {
	this.gravity = gravity;
    }
    
    public double getJumpForce() {
	return jumpForce;
    }
    
    public void setJumpForce(double jumpForce) {
	this.jumpForce = jumpForce;
    }
    
    public double getMaxFallSpeed() {
	return maxFallSpeed;
    }
    
    public void setMaxFallSpeed(double maxFallSpeed) {
	this.maxFallSpeed = maxFallSpeed;
    }
    
    public int getFacing() {
	return facing;
    }
    
    public Point2D.Double getCenter() {
	double centerX = bounds.x + (bounds.width / 2);
	double centerY = bounds.y + (bounds.height / 2);
	return new Point2D.Double(centerX, centerY);
    }
    
    public void addSpeedX(double add) {
	speedX += add;
	if (speedX < -maxSpeedX) {
	    speedX = -maxSpeedX;
	}
	if (speedX > maxSpeedX) {
	    speedX = maxSpeedX;
	}
	moveThisTickX = true;
    }
    
    public void addSpeedY(double add) {
	speedY += add;
	if (speedY < -maxSpeedY) {
	    speedY = -maxSpeedY;
	}
	if (speedY > maxSpeedY) {
	    speedY = maxSpeedY;
	}
	moveThisTickY = true;
    }
    
    public void stepMovement(ArrayList<Block> map, boolean descend) {
	
	steps++;
	
	/* Desired location */
	double goalX = speedX + this.getBounds().getX();
	double goalY = speedY + this.getBounds().getY();
	
	double finalPX = goalX;
	double finalPY = goalY;
	
	/* Don't bother if there's no problem */
	Block[] thingsToCheck = null;
	
	if ((thingsToCheck = intersectWithMap(new Bounds(this.getBounds().getX(), goalY, EntityPlayer.XSIZE, EntityPlayer.YSIZE), map)) != null) {
	    finalPY = handleVMovement(thingsToCheck, goalY, bounds.y, speedY, descend);
	}
	
	if ((thingsToCheck = intersectWithMap(new Bounds(goalX, finalPY, EntityPlayer.XSIZE, EntityPlayer.YSIZE), map)) != null) {
	    finalPX = handleHMovement(thingsToCheck, goalX, bounds.x, speedX);
	}
	
	if (finalPX < bounds.x) {
	    facing = Game.DIR_RIGHT;
	}
	if (finalPX > bounds.x) {
	    facing = Game.DIR_LEFT;
	}
	
	bounds.x = finalPX;
	bounds.y = finalPY;
	
	if (!moveThisTickX) {
	    if (Math.abs(speedX) < friction) {
		speedX = 0;
	    } else {
		if (speedX > 0) {
		    speedX -= friction;
		}
		if (speedX < 0) {
		    speedX += friction;
		}
	    }
	}
	if (!moveThisTickY) {
	    speedY += gravity;
	}
	
	if (this.getSpeedY() > maxFallSpeed) {
	    this.setSpeedY(maxFallSpeed);
	}
	
	moveThisTickX = false;
	moveThisTickY = false;
    }
    
    private Block[] intersectWithMap(Bounds entityRect, ArrayList<Block> map) {
	ArrayList<Block> intersect = new ArrayList<Block>();
	
	for (int i = 0; i < map.size(); i++) {
	    if (map.get(i).isSolid()) {
		Bounds blockRect = map.get(i).getBounds();
		if (entityRect.intersects(blockRect)) {
		    intersect.add(map.get(i));
		}
	    }
	}
	if (intersect.size() == 0) { return null; }
	return intersect.toArray(new Block[intersect.size()]);
    }
    
    private double handleHMovement(Block[] thingsToCheck, double goalX, double originalX, double rx) {
	for (int i = 0; i < thingsToCheck.length; i++) {
	    if (thingsToCheck[i] instanceof BlockPlatform) {
		continue;
	    }
	    double rightBound = thingsToCheck[i].getBounds().getX() + thingsToCheck[i].getBounds().getWidth();
	    double leftBound = thingsToCheck[i].getBounds().getX();
	    if (rx > 0) {
		/* --- RIGHT --- */
		if (rightBound > goalX) {
		    goalX = leftBound - this.getBounds().getWidth();
		    this.setSpeedX(0);
		}
	    }
	    if (rx < 0) {
		/* --- LEFT --- */
		if (goalX < rightBound) {
		    goalX = rightBound;
		    this.setSpeedX(0);
		}
	    }
	}
	return goalX;
    }
    
    private double handleVMovement(Block[] thingsToCheck, double goalY, double originalY, double ry, boolean descend) {
	for (int i = 0; i < thingsToCheck.length; i++) {
	    double downBound = thingsToCheck[i].getBounds().getY() + thingsToCheck[i].getBounds().getHeight();
	    double upBound = thingsToCheck[i].getBounds().getY();
	    if (ry > 0) {
		/* --- DOWN --- */
		if (downBound > goalY) {
		    if (thingsToCheck[i] instanceof BlockPlatform && (originalY + bounds.height > thingsToCheck[i].getBounds().y || descend)) {
			continue;
		    }
		    goalY = upBound - this.getBounds().getHeight();
		    this.setSpeedY(0);
		}
	    }
	    if (ry < 0) {
		/* --- UP --- */
		if (thingsToCheck[i] instanceof BlockPlatform) {
		    continue;
		}
		if (goalY < downBound) {
		    goalY = downBound;
		    this.setSpeedY(0);
		}
	    }
	}
	return goalY;
    }
    
    public void jump() {
	speedY = -jumpForce;
    }
}
