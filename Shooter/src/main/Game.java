package main;

public class Game {
	
	private GameWindow GameWindow;
	private GamePanel GamePanel;
	
	public Game() {
		
		GamePanel = new GamePanel();
		GameWindow = new GameWindow(GamePanel);
		GamePanel.requestFocus();
		
	}
}
