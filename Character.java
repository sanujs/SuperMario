import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

public class Character extends Sprite {
	private ClassLoader classLoader;
	private InputStream input;
	private String[] runningRight;
	private String[] runningLeft;
	private Image mario;
	private boolean right, running, jumping, wallLeft, wallRight;
	private Timer runTimer, jumpTimer, deathTimer;
	private int jumpCounter = 0, animation = 0, moveSpeed, runCounter = 0, shiftMap, deathCounter = 0;
	private final int JUMP_INCREMENT, START_SPEED, JUMP_HEIGHT;

	public Character(){
		super();
		shiftMap = 11;
		START_SPEED = 2;
		x = 92;
		height = 40;
		y = FLOOR - height;
		width = 30;
		JUMP_INCREMENT = 12;
		JUMP_HEIGHT = 15;
		runTimer = new Timer();
		jumpTimer = new Timer();
		deathTimer = new Timer();
		running = false;
		right = true;
		wallLeft = false;
		wallRight = false;
		classLoader = Thread.currentThread().getContextClassLoader();
		setStanding();
		//Arrays to iterate through for his running animation
		runningRight = new String[2];
		runningRight[0] = "Rrunning1.png";
		runningRight[1] = "Rrunning2.png";
		runningLeft = new String[2];
		runningLeft[0] = "Lrunning1.png";
		runningLeft[1] = "Lrunning2.png";
		System.out.println("welcome");
	}
	/**
	 * Starts a timer that moves Mario to the right and animates his running
	 */
	public void moveRight() {
		right = true;
		if (!running) {
			moveSpeed = START_SPEED;
			runTimer.scheduleAtFixedRate(new TimerTask() {
				public void run() {
					runCounter ++;
					running = true;
					//If Mario reaches a certain x-coordinate, the map shifts instead of Mario
					if (x < 200) {
						x += moveSpeed;
					} else {
						x = 200;
						shiftMap ++;
					}
					//If Mario's not in the air, animate his running
					if (!jumping && landed) {
						input = classLoader.getResourceAsStream(runningRight[animation]);
						try {
							mario = ImageIO.read(input);
						} catch (IOException e) {
							e.printStackTrace();
						}
						//Animates Mario every third iteration
						if (runCounter % 3 == 0) {
							if (animation == 0) {
								animation ++;
							} else {
								animation--;
							}
						}
					}
					//Accelerates Mario's speed
					if (moveSpeed < 10) {
						moveSpeed ++;
					}
				}
			}, 0, 25);
		}
	}
	/**
	 * Starts a timer that moves Mario to the left and animates his running
	 */
	public void moveLeft() {
		right = false;
		if (!running) {
			moveSpeed = START_SPEED;
			runTimer.scheduleAtFixedRate(new TimerTask() {
				public void run() {
					runCounter ++;
					running = true;
					//Stops Mario from going too far left
					if (x > 0) {
						x -= moveSpeed;
					}
					if (!jumping && landed) {
						input = classLoader.getResourceAsStream(runningLeft[animation]);
						try {
							mario = ImageIO.read(input);
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
					if (moveSpeed < 10) {
						moveSpeed ++;
					}
				}
			}, 0, 25);
		}
	}
	/**
	 * Starts a timer that moves Mario up and changes his picture (when the user clicks up)
	 */
	public void jump() {
		if (!jumping && landed) {
			jumpTimer.scheduleAtFixedRate(new TimerTask() {
				public void run() {
					jumping = true;
					jumpCounter ++;
					if (jumpCounter < JUMP_HEIGHT) {
						landed = true;
						y -= JUMP_INCREMENT;
						setJump();
					} else if (jumpCounter >= JUMP_HEIGHT) {
						landed = false;
						
						stopJumpTimer();
					}
				}
			}, 0, 25);
		}
	}
	/**
	 * Starts a timer that moves Mario up a third of the original height (when the user jumps on an enemy)
	 */
	public void littleJump() {
		stopJumpTimer();
		jumpTimer.scheduleAtFixedRate(new TimerTask() {
			public void run(){
				jumping = true;
				jumpCounter ++;
				if (jumpCounter < JUMP_HEIGHT / 3) {
					landed = true;
					y -= JUMP_INCREMENT;
					setJump();
				} else if (jumpCounter >= JUMP_HEIGHT/3) {
					landed = false;
					
					stopJumpTimer();
				}
			}
		}, 0, 25);
	}
	/**
	 * Stops Mario from running and resets the run timer
	 */
	public void stopRunTimer() {
		runTimer.cancel();
		runTimer = new Timer();
		running = false;
		runCounter = 0;
		if (landed) {
			setStanding();
		}
	}
	/**
	 * Stops Mario from jumping and resets the jump timer
	 */
	public void stopJumpTimer() {
		jumpTimer.cancel();
		jumpTimer = new Timer();
		jumping = false;
		jumpCounter = 0;
	}
	/**
	 * Sets the appropriate image of Mario when he is jumping
	 */
	public void setJump() {
		if (right) {
			input = classLoader.getResourceAsStream("jumpR.png");
			try {
				mario = ImageIO.read(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			input = classLoader.getResourceAsStream("jumpL.png");
			try {
				mario = ImageIO.read(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	/**
	 * Sets the appropriate image of Mario when he is standing, and stops him from falling
	 */
	public void setStanding() {
		if (right) {
			input = classLoader.getResourceAsStream("Rstanding.png");
			try {
				mario = ImageIO.read(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			input = classLoader.getResourceAsStream("Lstanding.png");
			try {
				mario = ImageIO.read(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		moveSpeed = 0;
		landed = true;
	}
	/**
	 * Mario's death animation
	 */
	public void die(){
		if (!dead) {
			deathTimer.scheduleAtFixedRate(new TimerTask(){
				public void run(){
					dead =  true;
					stopRunTimer();
					deathCounter ++;
					input = classLoader.getResourceAsStream("dead-mario.png");
					try {
						mario = ImageIO.read(input);
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (deathCounter <= JUMP_HEIGHT * 5) {
						landed = true;
						y -= JUMP_INCREMENT;
					} else if (deathCounter <= JUMP_HEIGHT * 6) {
						landed = false;
					}
				}
			}, 0, 150);
		}
		
	}
	/**
	 * Returns Mario's current image
	 */
	public Image getImage() {
		return mario;
	}
	/**
	 * Returns if Mario is running
	 */
	public boolean isRunning() {
		return running;
	}
	/**
	 * Returns Mario's speed
	 */
	public int getMoveSpeed(){
		return moveSpeed;
	}
	/**
	 * Returns true if the map needs to be shifted
	 */
	public boolean moveMap() {
		if (shiftMap % 2 == 0) {
			shiftMap ++;
			return true;
		}
		return false;
	}
	/**
	 * Returns if Mario is jumping
	 */
	public boolean isJumping(){
		return jumping;
	}
}
