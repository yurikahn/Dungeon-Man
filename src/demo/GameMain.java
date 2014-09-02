package demo;

import java.util.Random;

import javax.swing.JFrame;

/**
 * Just the launcher class for the 
 * game. Also owns the window object.
 * 
 * @author Moore
 *
 */
public class GameMain {

	public static Random RANDOM;
	
	/** 
	 * Game entry point
	 * @param args
	 */
	public static void main(String[] args)    {
		/* Frames per second to run at. Be careful messing with this - it is also steps per second */
		int FPS = 60;
		
		/* Seed RNG for the game */

		RANDOM =  new Random();
		int seed = RANDOM.nextInt();
		
		/* --- MANUAL SEED SET ---  ON=*/
		//seed = 1170030239;
		//seed = -1701330170;
		//seed = 178617516;
		//seed = 378015574;
		//seed = -2059765841;
		/**/
		
		System.out.println("World Seed: " + seed);
		RANDOM =  new Random(seed);
		
		/* Create the game interface */
		GamePanel instance = new GamePanel(DungeonRoom.DEFAULT_ROOM_X*GameRenderer.PIXEL_SIZE_BLOCK, DungeonRoom.DEFAULT_ROOM_Y*GameRenderer.PIXEL_SIZE_BLOCK);
		
		/* Create the frame */
		JFrame frame = new JFrame("Dungeon Crawler");
		
		/* Init frame, add game interface */
		frame.add(instance);
		frame.setVisible(true);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		/* Start the game loop */
		while(true) {
			instance.step();
			/* Artificially slow things down - step calculation is fairly quick. */
			try{Thread.sleep((1000/FPS));}catch(Exception ignore) {}
		}
	}

}
