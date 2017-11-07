import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class Pipe extends GameObject {
	//An enum with 3 sizes, each with a value of height
	public enum Size{ 
		SHORT (73), MEDIUM (104), TALL (138);
		private int height;
		private Size(int height) {
			this.height = height;}
	}

	private Size size;
	private ClassLoader classLoader;
	private InputStream input;
	private Image img;


	public Pipe(int x){
		classLoader = Thread.currentThread().getContextClassLoader();
		this.x = x;
		setShort();
		width = 75;
		topPlatformX = new int[width];
	}

	/**
	 * Sets the size of the pipe to short
	 */
	public void setShort(){
		size = Size.SHORT;
		height = size.height;
		y = FLOOR - height;
		input = classLoader.getResourceAsStream("small-pipe.png");
		try {
			img = ImageIO.read(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Sets the size of the pipe to medium
	 */
	public void setMedium(){
		size = Size.MEDIUM;
		height = size.height;
		y = FLOOR - height;
		input = classLoader.getResourceAsStream("medium-pipe.png");
		try {
			img = ImageIO.read(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Sets the size of the pipe to tall
	 */
	public void setTall(){
		size = Size.TALL;
		height = size.height;
		y = FLOOR - height;
		input = classLoader.getResourceAsStream("large-pipe.png");
		try {
			img = ImageIO.read(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public Image getImage() {
		return img;
	}

}
