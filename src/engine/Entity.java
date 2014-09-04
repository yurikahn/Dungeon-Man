package engine;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import demo.BlockPlatform;
import demo.EntityPlayer;
import demo.Game;
import demo.GameRenderer;

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
    private boolean onGround;
    
    private boolean moveThisTickX;
    private boolean moveThisTickY;
    
    private long    steps;
    
    private Image[] run;
    private Image[] idle;
    private Image[] jump;
    
    private Image[] currentAnimation;
    private int     animationStep;
    
    public Entity(Bounds bounds, String sprites, double speedX, double speedY, double maxSpeedX, double maxSpeedY, double friction, double acceleration, double gravity, double maxFallSpeed, double jumpForce) {
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
	
	initAnimation(sprites);
	currentAnimation = idle;
	animationStep = 0;
    }
    
    private void initAnimation(String sprites) {
	Image[] sheet = new Image[65];
	Image playerSheet = null;
	int playerHeight = 64;
	int playerWidth = 64;
	try {
	    playerSheet = ImageIO.read(new File(sprites));
	} catch (IOException e) {}
	if (playerSheet == null) {
	    sheet = null;
	} else {
	    for (int i = 0; i < sheet.length; i++) {
		int hoff = i / 8;
		sheet[i] = ((BufferedImage) playerSheet).getSubimage((i % 8) * playerWidth, playerHeight * hoff, playerWidth, playerHeight);
	    }
	}
	
	run = loadAnimation(playerSheet, 4, 12);
	jump = loadAnimation(playerSheet, 42, 46); // 42, 48
	idle = loadAnimation(playerSheet, 64, 65);
    }
    
    private Image[] loadAnimation(Image sheet, int start, int end) {
	int playerHeight = 64;
	int playerWidth = 64;
	Image[] animation = new Image[end - start];
	for (int i = start; i < end; i++) {
	    int hoff = i / 8;
	    animation[i - start] = ((BufferedImage) sheet).getSubimage((i % 8) * playerWidth, playerHeight * hoff, playerWidth, playerHeight);
	}
	return animation;
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
    
    public boolean getOnGround() {
	return onGround;
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
	onGround = false;
	
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
		    onGround = true;
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
	animationStep = 0;
	currentAnimation = jump;
    }
    
    public void render(Graphics2D g2d, int xOffset, int yOffset) {
	boolean boundBoxes = false;
	if (boundBoxes) {
	    g2d.setColor(GameRenderer.COLOR_RED);
	    g2d.fillRect((int) (bounds.x * GameRenderer.PIXEL_SIZE_BLOCK) - xOffset, (int) (bounds.y * GameRenderer.PIXEL_SIZE_BLOCK) - yOffset, (int) (GameRenderer.PIXEL_SIZE_BLOCK * bounds.width), (int) (GameRenderer.PIXEL_SIZE_BLOCK * bounds.height));
	    g2d.setColor(GameRenderer.COLOR_YELLOW);
	    if (facing == Game.DIR_LEFT) {
		g2d.fillRect((int) ((bounds.x + bounds.width - 0.2) * GameRenderer.PIXEL_SIZE_BLOCK) - xOffset, (int) (bounds.y * GameRenderer.PIXEL_SIZE_BLOCK) - yOffset, (int) (GameRenderer.PIXEL_SIZE_BLOCK * 0.2),
			(int) (GameRenderer.PIXEL_SIZE_BLOCK * (bounds.height)));
	    } else {
		g2d.fillRect((int) (bounds.x * GameRenderer.PIXEL_SIZE_BLOCK) - xOffset, (int) (bounds.y * GameRenderer.PIXEL_SIZE_BLOCK) - yOffset, (int) (GameRenderer.PIXEL_SIZE_BLOCK * 0.2), (int) (GameRenderer.PIXEL_SIZE_BLOCK * bounds.height));
	    }
	}
	animationStep++;
	int currentStep = (animationStep / 5);
	if (currentAnimation.equals(jump)) {
	    currentStep = Math.min(currentStep, currentAnimation.length - 1);
	}
	currentStep %= currentAnimation.length;
	Image imageToRender = currentAnimation[currentStep];
	
	int renderX = (int) (bounds.x * GameRenderer.PIXEL_SIZE_BLOCK) - xOffset - 60;
	int renderY = (int) (bounds.y * GameRenderer.PIXEL_SIZE_BLOCK) - yOffset;
	int renderWidth = 3 * GameRenderer.PIXEL_SIZE_BLOCK;
	int renderHeight = 3 * GameRenderer.PIXEL_SIZE_BLOCK;
	if (onGround && speedX == 0) {
	    currentAnimation = idle;
	} else if (!onGround) {
	    if (!currentAnimation.equals(jump)) {
		animationStep = 4;
	    }
	    currentAnimation = jump;
	} else {
	    currentAnimation = run;
	}
	if (facing == Game.DIR_RIGHT) {
	    imageToRender = GameRenderer.getFlippedImage((BufferedImage) (imageToRender));
	}
	
	g2d.drawImage(imageToRender, renderX, renderY, renderWidth, renderHeight, null);
    }
}
