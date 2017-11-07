/**
 * Sanuj Syal
 * Super Mario
 * 19/01/2017
 */
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Window extends JFrame implements KeyListener{
	private ClassLoader classLoader;
	private InputStream input;
	private BufferedImage icon;
	private Map map;
	private Character main;
	private Finish fin;
	private static final int CANVAS_SIZE = 500;
	private int[] pressed = new int[2];

	private ArrayList<Block> blocks;
	private ArrayList<GameObject> platforms;
	private ArrayList<Sprite> enemies;
	private ArrayList<Sprite> deadSprites;
	private ArrayList<Hole> holes;
	private int score = 0, coins = 0, time = 400;
	private int oldHighScore = 0;
	public boolean start = false, win = false;
	private Timer worldTimer;
	private File file;
	private Scanner highScore;
	private PrintWriter output;


	public Window(){
		//Setting the icon and title
		classLoader = Thread.currentThread().getContextClassLoader();	
		input = classLoader.getResourceAsStream("jumpR.png");
		try {
			icon = ImageIO.read(input);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		setIconImage(icon);
		setTitle("Muper Sario!");
		//Check for or create a file for high scores
		file = new File("highscore.txt");
		if (file.exists()) {
			try {
				highScore = new Scanner(file);
				oldHighScore = highScore.nextInt();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			try {
				file.createNewFile();
				saveHighScore();
			} catch (IOException e) {
				System.out.println("Cannot create high score file");
				System.out.println(e.getMessage());
			}
		}

		//Constructing the map
		map = new Map();
		addKeyListener(this);
		Container cp = getContentPane();
		cp.add(map);
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		setResizable(false);
		worldTimer = new Timer();

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (start) {
			/*
			 * Pressed array is made to keep track of up to two keys being pressed, specifically 
			 * if the user wants to run & jump at the same time.
			 */
			if (pressed[0] != 0) {
				pressed[1] = e.getKeyCode();
			} else {
				pressed[0] = e.getKeyCode();
			}

			switch(pressed[0]) {
			case KeyEvent.VK_LEFT:
				if (pressed[1] == KeyEvent.VK_UP) {
					main.jump();
				} else {
					main.moveLeft();
				}

				break;
			case KeyEvent.VK_RIGHT:

				if (pressed[1] == KeyEvent.VK_UP) {
					main.jump();
				} else {
					main.moveRight();
				}
				break;
			case KeyEvent.VK_UP:
				main.jump();
				if (pressed[1] == KeyEvent.VK_RIGHT) {
					main.moveRight();
				}
				if (pressed[1] == KeyEvent.VK_LEFT) {
					main.moveLeft();
				}
				break;

			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (start) {
			//Releasing keys stops the running and removes the keys from the arrays
			switch(e.getKeyCode()) {
			case KeyEvent.VK_RIGHT:
				main.stopRunTimer();
				break;
			case KeyEvent.VK_LEFT:
				main.stopRunTimer();
				break;			
			}
			if (e.getKeyCode() == pressed[0]) {
				pressed[0] = 0;
			} 
			if (e.getKeyCode() == pressed[1]) {
				pressed[1] = 0;
			}
		}


	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	/**
	 * Is constantly ran so that if Mario is not on a platform, he falls.
	 */
	public void fall(){
		boolean onBlock = false;
		boolean onHole = false;
		//Updates the position arrays in all the blocks
		for (int i = 0; i < platforms.size(); i++) {
			platforms.get(i).updateArray();
		}
		//Iterates through the array of blocks and checks if Mario is on any of them. (Planning to change so it works for any Game Object)
		for (int i = 0; i < platforms.size(); i++) {
			for (int j = 0; j < platforms.get(i).getTopPlatformX().length; j++) {
				if (main.x == platforms.get(i).getTopPlatformX()[j] || main.x + main.width == platforms.get(i).getTopPlatformX()[j]) {
					//Due to the fact that Mario falls at a rate, we need to look for Mario's y-position in a range
					if ((main.y + main.height) >= platforms.get(i).y && (main.y + main.height) < platforms.get(i).y + 15) {
						//if Mario was falling, stop him from falling, and put Mario on top of the block
						if (!main.landed && !main.dead) {
							main.setStanding();
							main.y = platforms.get(i).getTopPlatformY()[i] - main.height;
						}
						//Sets the local boolean variable to true
						onBlock = true;
					}
				} 
			}
		}
		//After iterating through the blocks, if mario is not on a block then check if he is on over a hole.
		if (!onBlock) {
			for (int i = 0; i < holes.size(); i ++) {
				if (main.x > holes.get(i).x && main.x + main.width < holes.get(i).x + holes.get(i).width) {
					onHole = true;
				}
			}
			if (main.y >= 410 && !main.dead && !onHole) {
				if (!main.landed){
					main.y = 410;
					main.setStanding();
				}
				//If Mario is not falling and is not jumping, and he is not on any of the platforms from above, make Mario fall.
			} else if (main.landed && !main.isJumping()) {
				main.landed = false;
			}

		}

	}
	/**
	 * Moves the map and all the elements backward. This gives the illusion of Mario moving forwards through the map.
	 */
	public void moveForward() {
		if (main.moveMap()){
			//Moves the map and all the elements backwards at the same rate that Mario would be moving forwards
			map.x -= main.getMoveSpeed();
			for (int i = 0; i < enemies.size(); i++) {
				enemies.get(i).x -= main.getMoveSpeed();
				if (enemies.get(i).x + enemies.get(i).width < 0) {
					enemies.remove(i);
				}
			}
			for (int i = 0; i < deadSprites.size(); i ++) {
				deadSprites.get(i).x -= main.getMoveSpeed();
				if (deadSprites.get(i).x + deadSprites.get(i).width < 0) {
					deadSprites.remove(i);
				}
			}
			for (int i = 0; i < platforms.size(); i ++) {
				platforms.get(i).x -= main.getMoveSpeed();
				platforms.get(i).updateArray();
				if (platforms.get(i).x + platforms.get(i).width < 0) {
					platforms.remove(i);
				}
			}
			for (int i = 0; i < blocks.size(); i ++) {
				if (blocks.get(i).x + blocks.get(i).width < 0) {
					blocks.remove(i);
				}
			}
			for (int i = 0; i < holes.size(); i ++) {
				holes.get(i).x -= main.getMoveSpeed();
				if (holes.get(i).x + holes.get(i).width < 0) {
					holes.remove(i);
				}
			}
			fin.x -= main.getMoveSpeed();
			//If Mario reaches the finish line, save the score
			if (main.x + main.width >= fin.x) {
				main.stopRunTimer();
				score += 5000;
				score += time * 100;
				win = true;
				JOptionPane.showMessageDialog(this, "You Win! Your score is " + score);
				if (score > oldHighScore) {
					saveHighScore();
				}
			}
		}
		repaint();
	}
	/**
	 * Is constantly ran to check if Mario hits any other element
	 */
	public void collision(){
		//Block Collision
		//Checks if Mario hits a block from below
		for (int i = 0; i < blocks.size(); i++) {
			for (int j = 0; j < blocks.get(i).getBottomPlatformX().length; j++) {
				if (main.x + main.width/2 == blocks.get(i).getBottomPlatformX()[j]) {
					if (main.y < blocks.get(i).getBottomPlatformY()[i] && main.y > (blocks.get(i).getBottomPlatformY()[i] - blocks.get(i).height + 10)) {
						//If the top of Mario hits the bottom of a block, stop him from jumping, let him fall, and make the block bounce
						blocks.get(i).bounce();
						main.landed = false;
						main.stopJumpTimer();
						//Add to score if Mario hits a question block
						if (blocks.get(i).isQuestion()) {
							coins ++;
							score += 200;
						}
					}
				}
			}
		}
		//Wall Collision
		for (int i= 0; i < platforms.size(); i ++){
			//Stops Mario from going through walls
			if (main.y <= platforms.get(i).y + platforms.get(i).height && main.y + main.height > platforms.get(i).y) {
				if (main.x + main.width >= platforms.get(i).x && main.x + main.width < platforms.get(i).x + 11) {
					main.x = platforms.get(i).x - main.width;
				} else if (main.x <= platforms.get(i).x + platforms.get(i).width && main.x >= platforms.get(i).x + platforms.get(i).width - 11){
					main.x = platforms.get(i).x + platforms.get(i).width;
				}
			}
			//Makes enemies bounce off of walls
			for (int j = 0; j < enemies.size(); j++) {
				if (enemies.get(j).y <= platforms.get(i).y + platforms.get(i).height && enemies.get(j).y + enemies.get(j).height > platforms.get(i).y) {
					if (enemies.get(j).x + enemies.get(j).width >= platforms.get(i).x && enemies.get(j).x < platforms.get(i).x) {
						enemies.get(j).x = platforms.get(i).x - enemies.get(j).width;
						enemies.get(j).moveLeft();
					} else if (enemies.get(j).x <= platforms.get(i).x + platforms.get(i).width && enemies.get(j).x + enemies.get(j).width >= platforms.get(i).x + platforms.get(i).width){
						enemies.get(j).x = platforms.get(i).x + platforms.get(i).width;
						enemies.get(j).moveRight();
					}
				}
			}
		}
		//Enemy Collision
		//Iterates through the enemies to see if Mario is touching any of them
		for (int i = 0; i < enemies.size(); i ++) {
			if (main.y + main.height >= enemies.get(i).y && main.y <= enemies.get(i).y + enemies.get(i).height) {
				if (main.x + main.width >= enemies.get(i).x && main.x <= enemies.get(i).x + enemies.get(i).width) {
					//If Mario is falling while he is touching an enemy then kill the enemy, otherwise Mario dies.
					if (!main.landed) {
						enemies.get(i).die();
						main.littleJump();
						//Adds dead sprites to the deadSprites array so that they can run their death animation without interfering with Mario
						deadSprites.add(enemies.get(i));
						enemies.remove(i);
						score += 100;
					}else {
						//Mario dies
						main.die();
					}
				}
			}

		}
	}
	/**
	 * Writes the new highscore to the text file
	 */
	public void saveHighScore(){
		try {
			output = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		output.println(score);
		output.close();
	}
	/**
	 * Returns true if Mario falls below the map 
	 */
	public boolean isDead(){
		if( main.y > CANVAS_SIZE){
			return true;
		}
		return false;
	}
	/**
	 * Returns true if Mario reaches the finish
	 */
	public boolean win(){
		return win;
	}
	/**
	 * Starts the game timer
	 */
	public void time(){
		worldTimer.scheduleAtFixedRate(new TimerTask(){
			public void run(){
				time --;
			}
		}, 0, 1000);
	}

	private class Map extends JPanel implements ActionListener{
		private Image background, coin, title;
		private int x, y;
		private ClassLoader classLoader;
		private InputStream input;
		private Block blk1, blk2, blk3, blk4, blk5, blk6, blk7, blk8, blk9, blk10, blk11, blk12, blk13, blk14, blk15, blk16, blk17, blk18, blk19, blk20, blk21, blk22, blk23, blk24, blk25, blk26, blk27;
		private Block blk28, blk29, blk30, blk31, blk32, blk33, blk34, blk35, blk36, blk37, blk38, blk39;
		private Goomba gba1, gba2, gba3, gba4;
		private Hole h1, h2, h3;
		private Pipe p1, p2, p3, p4;
		private JLabel instructions1, instructions2, lblTop;
		private JButton btnPlay, btnInst, btnBack;

		public Map(){


			//Build arrays for objects and sprites in the game
			blocks = new ArrayList<Block>();
			enemies = new ArrayList<Sprite>();
			platforms = new ArrayList<GameObject>();
			deadSprites = new ArrayList<Sprite>();
			holes = new ArrayList<Hole>();
			x = 0;
			y = 0;
			setPreferredSize(new Dimension(CANVAS_SIZE, CANVAS_SIZE));
			//Initiate all the objects and sprites
			main = new Character();
			blk1 = new Block(567);
			blk1.setQuestion();
			blocks.add(blk1);
			platforms.add(blk1);
			blk2 = new Block(blocks.get(0).x + 143);
			blocks.add(blk2);
			platforms.add(blk2);
			blk3 = new Block(blocks.get(1).x + blocks.get(1).width);
			blocks.add(blk3);
			platforms.add(blk3);
			blk3.setQuestion();
			blk4 = new Block(blocks.get(2).x + blocks.get(2).width);
			blocks.add(blk4);
			platforms.add(blk4);
			blk5 = new Block(blocks.get(3).x + blocks.get(3).width);
			blocks.add(blk5);
			platforms.add(blk5);
			blk5.setQuestion();
			blk6 = new Block(blocks.get(4).x + blocks.get(4).width);
			blocks.add(blk6);
			platforms.add(blk6);
			blk7 = new Block(blocks.get(3).x, 160);
			blocks.add(blk7);
			platforms.add(blk7);
			blk7.setQuestion();
			p1 = new Pipe(1000);
			platforms.add(p1);
			p2 = new Pipe(p1.x + 340);
			p2.setMedium();
			platforms.add(p2);
			gba2 = new Goomba(p2.x + 160);
			enemies.add(gba2);
			p3 = new Pipe(p2.x + 290);
			p3.setTall();
			platforms.add(p3);
			gba3 = new Goomba(p3.x +150);
			enemies.add(gba3);
			gba4 = new Goomba(gba3.x + 60);
			enemies.add(gba4);
			p4 = new Pipe(p3.x + 390);
			p4.setTall();
			platforms.add(p4);
			blk8 = new Block(p4.x + 250, 265);
			blocks.add(blk8);
			platforms.add(blk8);
			blk8.setQuestion();
			h1 = new Hole(blk8.x + 180, 70);
			holes.add(h1);
			blk9 = new Block(blk8.x + 450);
			blocks.add(blk9);
			platforms.add(blk9);
			blk10 = new Block(blk9.x + blk9.width + 10);
			blocks.add(blk10);
			platforms.add(blk10);
			blk10.setQuestion();
			blk11 = new Block(blk10.x + blk10.width);
			blocks.add(blk11);
			platforms.add(blk11);
			blk12 = new Block(blk11.x + blk11.width, 160);
			blocks.add(blk12);
			platforms.add(blk12);
			blk13 = new Block(blk12.x + blk12.width, 160);
			blocks.add(blk13);
			platforms.add(blk13);
			blk14 = new Block(blk13.x + blk13.width, 160);
			blocks.add(blk14);
			platforms.add(blk14);
			blk15 = new Block(blk14.x + blk14.width, 160);
			blocks.add(blk15);
			platforms.add(blk15);
			blk16 = new Block(blk15.x + blk15.width, 160);
			blocks.add(blk16);
			platforms.add(blk16);
			blk17 = new Block(blk16.x + blk16.width, 160);
			blocks.add(blk17);
			platforms.add(blk17);	
			blk18 = new Block(blk17.x + blk17.width, 160);
			blocks.add(blk18);
			platforms.add(blk18);	
			blk19 = new Block(blk18.x + blk18.width, 160);
			h2 = new Hole(blk18.x + 5, 100);
			holes.add(h2);
			blocks.add(blk19);
			platforms.add(blk19);
			blk20 = new Block(blk19.x + 35*4, 160);
			blocks.add(blk20);
			platforms.add(blk20);
			blk21 = new Block(blk20.x + blk20.width, 160);
			blocks.add(blk21);
			platforms.add(blk21);
			blk22 = new Block(blk21.x + blk21.width, 160);
			blocks.add(blk22);
			platforms.add(blk22);
			blk22.setQuestion();
			blk23 = new Block(blk22.x);
			blocks.add(blk23);
			platforms.add(blk23);
			blk24= new Block(3540);
			blocks.add(blk24);
			platforms.add(blk24);
			blk25 = new Block(blk24.x + blk24.width);
			blk25.setQuestion();
			blocks.add(blk25);
			platforms.add(blk25);
			blk26 = new Block(blk25.x + blk25.width*5);
			blk26.setQuestion();
			blocks.add(blk26);
			platforms.add(blk26);
			blk27 = new Block(blk26.x + blk26.width*3);
			blk27.setQuestion();
			blocks.add(blk27);
			platforms.add(blk27);
			blk28 = new Block(blk27.x, 160);
			blk28.setQuestion();
			blocks.add(blk28);
			platforms.add(blk28);
			blk29 = new Block(blk28.x + blk28.width*3);
			blk29.setQuestion();
			blocks.add(blk29);
			platforms.add(blk29);
			blk30 = new Block(blk29.x + blk29.width*6);
			blocks.add(blk30);
			platforms.add(blk30);
			blk31 = new Block(blk30.x + blk30.width*3, 160);
			blocks.add(blk31);
			platforms.add(blk31);
			blk32 = new Block(blk31.x + blk31.width, 160);
			blocks.add(blk32);
			platforms.add(blk32);
			blk33 = new Block(blk32.x + blk32.width, 160);
			blocks.add(blk33);
			platforms.add(blk33);
			blk34 = new Block(blk33.x + blk33.width*5, 160);
			blocks.add(blk34);
			platforms.add(blk34);
			blk35 = new Block(blk34.x + blk34.width, 160);
			blk35.setQuestion();
			blocks.add(blk35);
			platforms.add(blk35);
			blk36 = new Block(blk35.x + blk35.width, 160);
			blk36.setQuestion();
			blocks.add(blk36);
			platforms.add(blk36);
			blk37 = new Block(blk36.x + blk36.width, 160);
			blocks.add(blk37);
			platforms.add(blk37);
			blk38 = new Block(blk35.x);
			blocks.add(blk38);
			platforms.add(blk38);
			blk39 = new Block(blk36.x);
			blocks.add(blk39);
			platforms.add(blk39);
			h3 = new Hole(blk39.x + 829, 70);
			holes.add(h3);
			fin = new Finish(7040);
			//Set the image of the game
			classLoader = Thread.currentThread().getContextClassLoader();
			input = classLoader.getResourceAsStream("mario.png");
			try {
				background = ImageIO.read(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
			input = classLoader.getResourceAsStream("coin.png");
			try {
				coin = ImageIO.read(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
			input = classLoader.getResourceAsStream("title.png");
			try {
				title = ImageIO.read(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
			//Build the menu
			setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			add(Box.createVerticalStrut(240));
			lblTop = new JLabel("Top - " + oldHighScore);
			lblTop.setFont(new Font("Courier New", Font.BOLD, 18));
			lblTop.setForeground(Color.WHITE);
			lblTop.setAlignmentX(CENTER_ALIGNMENT);
			add(lblTop);
			add(Box.createVerticalStrut(30));
			instructions1 = new JLabel("Control Sario with the arrow keys and");
			instructions1.setVisible(false);
			instructions1.setFont(new Font("Courier New", Font.PLAIN, 20));
			instructions1.setAlignmentX(CENTER_ALIGNMENT);
			add(instructions1);
			instructions2 = new JLabel("get him to the finish line unharmed.");
			instructions2.setVisible(false);
			instructions2.setFont(new Font("Courier New", Font.PLAIN, 20));
			instructions2.setAlignmentX(CENTER_ALIGNMENT);
			add(instructions2);
			btnPlay = new JButton("Play");
			btnPlay.setFocusable(false);
			btnPlay.setAlignmentX(CENTER_ALIGNMENT);
			btnPlay.addActionListener(this);
			add(btnPlay);
			add(Box.createVerticalStrut(20));
			btnInst = new JButton("Instructions");
			btnInst.setFocusable(false);
			btnInst.addActionListener(this);
			btnInst.setAlignmentX(CENTER_ALIGNMENT);
			add(btnInst);
			btnBack = new JButton("Back");
			btnBack.setVisible(false);
			btnBack.setFocusable(false);
			btnBack.addActionListener(this);
			btnBack.setAlignmentX(CENTER_ALIGNMENT);
			add(btnBack);
		}

		public void paintComponent(Graphics g) {
			//Draws the map and all the components.
			super.paintComponent(g);
			g.drawImage(background, x, y, CANVAS_SIZE*15, CANVAS_SIZE, this);
			g.drawImage(main.getImage(), main.x, main.y, main.width, main.height, this);
			for (int i = 0; i < platforms.size(); i++) {
				//Only draw the elements if they are in the screen
				if (platforms.get(i).x < CANVAS_SIZE && platforms.get(i).x + platforms.get(i).width >= 0) {
					g.drawImage(platforms.get(i).getImage(), platforms.get(i).x, platforms.get(i).y, platforms.get(i).width, platforms.get(i).height, this);
				}
			}
			for (int i = 0; i< enemies.size(); i++) {
				if (enemies.get(i).x < CANVAS_SIZE && enemies.get(i).x + enemies.get(i).width >= 0) {
					g.drawImage(enemies.get(i).getImage(), enemies.get(i).x, enemies.get(i).y, enemies.get(i).width, enemies.get(i).height, this);
				}
			}
			for (int i = 0; i < deadSprites.size(); i ++) {
				if (deadSprites.get(i).x < CANVAS_SIZE && deadSprites.get(i).x + deadSprites.get(i).width >= 0) {
					g.drawImage(deadSprites.get(i).getImage(), deadSprites.get(i).x, deadSprites.get(i).y, deadSprites.get(i).width, deadSprites.get(i).height, this);
				}
			}
			//Build the in game elements
			if (start) {
				g.setColor(Color.WHITE);
				g.setFont(new Font("Courier New", Font.BOLD, 25));
				g.drawString("SARIO", 30, 30);
				g.drawString(String.format("%06d", score), 30, 50);
				g.drawImage(coin, 160, 25, 20, 30, this);
				g.drawString("x" + String.format("%02d", coins), 185, 50);
				g.drawString("WORLD", 285, 30);
				g.drawString("1-1", 295, 50);
				g.drawString("TIME", 400, 30);
				g.drawString(String.format("%03d", time), 410, 50);
			}
			if (!start) {
				g.drawImage(title, 50, 20, 400, 200, this);
			}

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			//Code each individual button
			if (e.getSource() == btnPlay) {
				remove(btnPlay);
				remove(btnInst);
				lblTop.setVisible(false);
				start = true;
				time();
			} 
			if (e.getSource() == btnInst) {
				btnPlay.setVisible(false);
				btnInst.setVisible(false);
				instructions1.setVisible(true);
				instructions2.setVisible(true);
				btnBack.setVisible(true);
			}
			if (e.getSource() == btnBack) {
				instructions1.setVisible(false);
				instructions2.setVisible(false);
				btnPlay.setVisible(true);
				btnInst.setVisible(true);
				btnBack.setVisible(false);
			}
		}
	}



}