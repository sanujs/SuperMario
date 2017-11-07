import java.util.Timer;
import java.util.TimerTask;

public class Tester {
	private static Window w;
	public static void main(String[] args) {

		w = new Window();
		Timer worldTimer = new Timer();
		worldTimer.scheduleAtFixedRate(new TimerTask(){
			public void run(){
				w.moveForward();
				w.fall();
				w.collision();
				//When the game ends, restart the game.
				if (w.isDead() || w.win()) {
					w.dispose();
					w = new Window();
				}
			}
		}, 0, 5);

	}

}
