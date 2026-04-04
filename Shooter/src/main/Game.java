package main;

public class Game implements Runnable{
	
	private GameWindow GameWindow;
	private GamePanel GamePanel;
	private Thread GameThread; //GameLoop
	private final int FPS_SET = 120;
	
	public Game() {
		
		GamePanel = new GamePanel();
		GameWindow = new GameWindow(GamePanel);
		GamePanel.requestFocus();
		StartGameLoop();
		
	}
	
	private void StartGameLoop() {
		GameThread = new Thread(this);
		GameThread.start();
	}

	@Override
	public void run() {
		
		double TimePerFrame = 1000000000.0 / FPS_SET;
		long LastFrame = System.nanoTime();
		long now = System.nanoTime();
		int frames = 0;
		long LastCheck = System.currentTimeMillis()
;		
		while(true) {
			
			now = System.nanoTime();
			if(now - LastFrame >= TimePerFrame) {
				
				GamePanel.repaint();
				LastFrame = now;
				frames++;
				
				
			}
			
			if(System.currentTimeMillis() - LastCheck >= 1000) {
				LastCheck = System.currentTimeMillis();
				System.out.println("FPS: " + frames);
				frames = 0; //Reset Frame counter SO IT WON'T STACK UP!!!!
			}
		}
		
	}
}
