package main;

public class Game implements Runnable{
	
	private GameWindow GameWindow;
	private GamePanel GamePanel;
	private Thread GameThread; //GameLoop
	private final int FPS_SET = 120; //Frames per second
	private final int UPS_SET = 200; //Updates per second
	
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
	
	public void Update() {
		GamePanel.updateGame();
	}
	
	

	@Override
	public void run() {
		
		double TimePerFrame = 1000000000.0 / FPS_SET;
		double TimePerUpdate = 1000000000.0 / UPS_SET;

		long previousTime = System.nanoTime();
		
		int frames = 0;
		int updates = 0;
		long LastCheck = System.currentTimeMillis();
		
		double deltaU = 0;
		double deltaF = 0;


		while(true) {
			long currentTime = System.nanoTime();

			deltaU += (currentTime - previousTime) / TimePerUpdate;
			deltaF += (currentTime - previousTime) / TimePerFrame;

			previousTime = currentTime;
			
			
			if(deltaU >= 1) {
				Update();
				updates++;
				deltaU--;
			}
			if(deltaF >= 1) { //Remplazo del metodo anterior [68-72]
				GamePanel.repaint();
				frames++;
				deltaF--;
			}
			/*
			if(now - LastFrame >= TimePerFrame) {
				GamePanel.repaint();
				LastFrame = now;
				frames++;	
			}
			*/
			
			if(System.currentTimeMillis() - LastCheck >= 1000) {
				LastCheck = System.currentTimeMillis();
				System.out.println("FPS: " + frames + " | UPS: " + updates);
				frames = 0; //Reset Frame counter SO IT WON'T STACK UP!!!!
				updates = 0;
			}
		}
		
	}
}
