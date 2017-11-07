import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

public class Goomba extends Sprite{
	private ClassLoader classLoader;
	private InputStream input;
	private String[] moving;
	private Timer runTimer, deathTimer;
	private boolean running = false, right;

	private int animation = 0, runCounter = 0, deathCounter = 0;
	private Image goomba;

	//Constructs the Goomba with the default y position
	public Goomba(int x){
		this.x = x;
		y = 420;
		height = 30;
		width = 30;
		dead = false;
		deathTimer = new Timer();
		moving = new String[2];
		moving[0] = "goomba1.png";
		moving[1] = "goomba2.png";	
		classLoader = Thread.currentThread().getContextClassLoader();
		input = classLoader.getResourceAsStream(moving[animation]);
		try {
			goomba = ImageIO.read(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		moveLeft();
	}
	//Constructs the Goomba with a manual y position
	public Goomba(int x, int y){
		this.x = x;
		this.y = y;
		y = 420;
		dead = false;
		runTimer = new Timer();
		deathTimer = new Timer();
		moving = new String[2];
		moving[0] = "goomba1.png";
		moving[1] = "goomba2.png";	
		classLoader = Thread.currentThread().getContextClassLoader();
		input = classLoader.getResourceAsStream(moving[animation]);
		try {
			goomba = ImageIO.read(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		moveLeft();
	}

	/**
	 * Starts a timer that moves the Goomba to the right at each tick
	 */
	public void moveRight() {
		if (running) {
			runTimer.cancel();
		}
		running = true;
		runTimer = new Timer();
		runCounter = 0;
		runTimer.scheduleAtFixedRate(new TimerTask(){
			public void run() {
				runCounter ++;
				x++;
				input = classLoader.getResourceAsStream(moving[animation]);
				try {
					goomba = ImageIO.read(input);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (runCounter % 3 == 0) {
					if (animation == 0) {
						animation ++;
					} else {
						animation--;
					}
				}

			}
		}, 0, 25);

	}

	/**
	 * Starts a timer that moves the Goomba to the left at each tick
	 */
	public void moveLeft() {
		if (running) {
			runTimer.cancel();
		}
		running = true;
		runTimer = new Timer();
		runCounter = 0;
		runTimer.scheduleAtFixedRate(new TimerTask(){
			public void run() {
				runCounter ++;
				x--;
				input = classLoader.getResourceAsStream(moving[animation]);
				try {
					goomba = ImageIO.read(input);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (runCounter % 3 == 0) {
					if (animation == 0) {
						animation ++;
					} else {
						animation--;
					}
				}

			}
		}, 0, 25);

	}
	/**
	 * Runs a death animation which flattens the Goomba and makes his image flash
	 */
	public void die() {
		if (!dead) {
			runTimer.cancel();
			height = height / 2;
			y += height;
			deathTimer.scheduleAtFixedRate(new TimerTask(){

				@Override
				public void run() {
					dead = true;
					deathCounter ++;
					if (goomba == null) {
						input = classLoader.getResourceAsStream(moving[0]);
						try {
							goomba = ImageIO.read(input);
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						goomba = null;
					}
					if (deathCounter == 6) {
						goomba = null;
						cancel();
					}
				}


			}, 0, 150);

		}
	}

	@Override
	public Image getImage() {
		// TODO Auto-generated method stub
		return goomba;
	}

}
