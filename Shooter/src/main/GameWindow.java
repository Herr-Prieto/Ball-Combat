package main;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

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
		
		jframe.addWindowFocusListener(new WindowFocusListener(){

			@Override
			public void windowGainedFocus(WindowEvent e) {
				
			}

			@Override
			public void windowLostFocus(WindowEvent e) {
					GamePanel.getGame().WindowFocusLost();
				
			}
			
			
			
		});
		
		
	}
}
