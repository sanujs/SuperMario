import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

public class Block extends GameObject{
	private ClassLoader classLoader;
	private InputStream input;
	private Image img;
	//Arrays that contain exact X positions of each pixel in the top and bottom platforms of the blocks
	private int[] bottomPlatformX;
	private Timer bounceTimer;
	private int bounceCounter = 0;
	private boolean bouncing;
	public enum Type{QUESTION, BRICK, OPENED};
	private Type type;


	public Block(int x, int y) {
		bounceTimer = new Timer();
		classLoader = Thread.currentThread().getContextClassLoader();		
		height = 35;
		width = height;
		topPlatformX = new int[width];
		bottomPlatformX = new int[width];
		this.x = x;
		this.y = y;
		updateArray();
		bouncing = false;
		setBrick();
	}

	public Block(int x) {
		bounceTimer = new Timer();
		classLoader = Thread.currentThread().getContextClassLoader();		
		height = 35;
		width = height;
		topPlatformX = new int[width];
		bottomPlatformX = new int[width];
		this.x = x;
		//Sets default y position
		y = 305;
		updateArray();
		bouncing = false;
		//Sets default image to a brick
		setBrick();
	}
	/**
	 * Updates top and bottom platform arrays with new x positions of the block when the map is shifted
	 */
	public void updateArray(){
		int counter = x;
		for (int i = 0; i < topPlatformX.length; i ++) {
			topPlatformX[i] = counter;
			counter ++;
		}
		bottomPlatformX = topPlatformX;
		
	}
	
	/**
	 * Sets the image and type of the block to a Question
	 */
	public void setQuestion(){
		input = classLoader.getResourceAsStream("question.JPG");
		try {
			img = ImageIO.read(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		type = Type.QUESTION;
	}
	/**
	 * Sets the image and type of the block to a Brick
	 */
	public void setBrick(){
		input = classLoader.getResourceAsStream("brick.jpg");
		try {
			img = ImageIO.read(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		type = Type.BRICK;
	}
	/**
	 * Sets the image and type of the block to a Opened
	 */
	public void setOpened(){
		input = classLoader.getResourceAsStream("opened_question.jpg");
		try {
			img = ImageIO.read(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		type = Type.OPENED;
	}
	/**
	 * Returns true if the block is a question block
	 */
	public boolean isQuestion() {
		if (type == Type.QUESTION) {
			return true;
		}
		return false;
	}
	/**
	 * Returns the block's image
	 */
	public Image getImage(){
		return img;
	}
	
	/**
	 * Returns the bottom platform array
	 */
	public int[] getBottomPlatformX() {
		return getTopPlatformX();
	}
	
	/**
	 * Creates and returns an array with the y position of the botttom of the block
	 */
	public int[] getBottomPlatformY() {
		int[] bottomPlatformY = new int[bottomPlatformX.length];
		for (int i = 0; i < bottomPlatformY.length; i ++){
			bottomPlatformY[i] = y + height;
		}
		return bottomPlatformY;
	}
	
	/**
	 * Bounces the block up (for when Mario hits a block from beneath)
	 */
	public void bounce(){
		if (!bouncing && type != Type.OPENED) {
			bounceTimer.scheduleAtFixedRate(new TimerTask(){
				public void run(){
					bouncing = true;
					bounceCounter ++;
					if (bounceCounter <= 2) {
						y -= 12;
					} else if (bounceCounter <= 4) {
						y+= 12;
					} else {
						stopBounceTimer();
						if (type == Type.QUESTION) {
							setOpened();
						}
					}
					
				}
			}, 0, 25);
		}
	}
	/**
	 * Stops the block from bouncing and resets the timer
	 */
	public void stopBounceTimer(){
		bouncing = false;
		bounceTimer.cancel();
		bounceTimer = new Timer();
		bounceCounter = 0;
	}
}
