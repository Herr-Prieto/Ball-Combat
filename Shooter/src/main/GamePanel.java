package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

import javax.swing.JPanel;

import inputs.KeyboardInputs;
import inputs.MouseInputs;

public class GamePanel extends JPanel{
	
	private MouseInputs MouseInputs;
	private float xDelta = 100, yDelta = 100;
	private float xDir = 1, yDir = 1;
	private Color Color = new Color(150, 20, 90);
	private Random Random;

	public GamePanel() {
		Random = new Random();
		MouseInputs = new MouseInputs(this);
		addKeyListener(new KeyboardInputs(this));
		addMouseListener(new MouseInputs(this));
		addMouseMotionListener(new MouseInputs(this));

	}
	
	public void changeXDelta(int value) {
		this.xDelta += value;
	}
	public void changeYDelta(int value) {
		this.yDelta += value;

	}
	
	public void setRectPos(int x, int y) {
		this.xDelta = x;
		this.yDelta = y;
		

	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		updateRectangle();
		g.setColor(Color);
		
		
		g.fillRect((int)xDelta, (int)yDelta, 200, 50);
		
			
	}

	private void updateRectangle() {
		xDelta+= xDir;
		if(xDelta > 400 || xDelta < 0) {
			xDir*=-1;
			Color = getRandomColor();
		}
		yDelta+= yDir;
		if(yDelta > 400 || yDelta < 0) {
			yDir*=-1;
		}
		
	}
	private java.awt.Color getRandomColor() {
		int r =Random.nextInt(255);
		int g =Random.nextInt(255);
		int b =Random.nextInt(255);
		
		return new Color(r, g, b);

	}
	
}
