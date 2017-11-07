import java.awt.Image;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

public abstract class Sprite {
	private Timer fallTimer;
	public int x, y, height, width;
	public final int FLOOR = 450;
	public boolean landed, dead;
	private final int FALL_INCREMENT;
	
	public Sprite(){
		fallTimer = new Timer();
		landed = true;
		FALL_INCREMENT = 12;
		fall();
	}
	/**
	 * Sets a timer that is always running, which makes the sprite fall if it is not on a platform.
	 */
	public void fall() {
		fallTimer.scheduleAtFixedRate(new TimerTask(){
			public void run() {
				if (!landed) {
					y += FALL_INCREMENT;
				}
			}
		}, 0, 25);
	}
public abstract void moveRight();
public abstract void moveLeft();
public abstract Image getImage();
public abstract void die();

}
