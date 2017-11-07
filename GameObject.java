import java.awt.Image;
import java.io.InputStream;

import javax.swing.JPanel;

public abstract class GameObject extends JPanel{

	public int x, y, height, width;
	public final int FLOOR = 446;
	public int[] topPlatformX;
	
	/**
	 * Updates the array for the top platform as the x position of the Game Object changes
	 */
	public void updateArray(){
		int counter = x;
		for (int i = 0; i < topPlatformX.length; i ++) {
			topPlatformX[i] = counter;
			counter ++;
		}	
	}
	/**
	 * Returns an array of the top platform's x positions
	 */
	public int[] getTopPlatformX() {
		return topPlatformX;
	}
	/**
	 * Returns an array of the top platform's y positions
	 */
	public int[] getTopPlatformY() {
		int[] topPlatformY = new int[getTopPlatformX().length];
		for (int i = 0; i < topPlatformY.length; i ++){
			topPlatformY[i] = y;
		}
		return topPlatformY;
	}
	/**
	 * Returns an array of the left side's x positions
	 */
	public int[] getLeftPlatformX(){
		int[] leftPlatformX = new int[height];
		for (int i = 0; i < leftPlatformX.length; i ++){
			leftPlatformX[i] = x;
		}
		return leftPlatformX;
	}
	/**
	 * Returns an array of the left side's y positions
	 */
	public int[] getLeftPlatformY(){
		int counter = y;
		int[] leftPlatformY = new int[height];
		for (int i = 0; i < leftPlatformY.length; i ++){
			leftPlatformY[i] = counter;
			counter ++;
		}
		return leftPlatformY;
	}
	/**
	 * Returns an array of the right side's x positions
	 */
	public int[] getRightPlatformX(){
		int[] rightPlatformX = new int[height];
		for (int i = 0; i < rightPlatformX.length; i ++){
			rightPlatformX[i] = x + width;
		}
		return rightPlatformX;
	}
	/**
	 * Returns an array of the right side's y positions
	 */
	public int[] getRightPlatformY(){
		return getLeftPlatformY();
	}
	public abstract Image getImage();

}