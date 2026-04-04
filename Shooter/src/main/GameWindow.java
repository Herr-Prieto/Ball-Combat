package main;

import javax.swing.JFrame;

public class GameWindow {
	
	private JFrame jframe;
	public GameWindow(GamePanel GamePanel){
		
		jframe = new JFrame();
		jframe.add(GamePanel);
		jframe.setLocationRelativeTo(null);
		jframe.pack();
		jframe.setResizable(false);
		jframe.setVisible(true);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
	}
}
