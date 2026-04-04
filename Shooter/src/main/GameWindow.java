package main;

import javax.swing.JFrame;

public class GameWindow {
	
	private JFrame jframe;
	public GameWindow(GamePanel GamePanel){
		
		jframe = new JFrame();
		jframe.setSize(400,400);
		jframe.add(GamePanel);
		jframe.setLocationRelativeTo(null);
		
		
		jframe.setVisible(true);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
	}
}
