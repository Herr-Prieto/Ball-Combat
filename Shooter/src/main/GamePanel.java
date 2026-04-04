package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import inputs.KeyboardInputs;
import inputs.MouseInputs;

public class GamePanel extends JPanel{
	
	private MouseInputs MouseInputs;
	private float xDelta = 100, yDelta = 100;
	private BufferedImage img, subImg;
	
	
	


	public GamePanel() {
		MouseInputs = new MouseInputs(this);
		SetPanelSize();
		addKeyListener(new KeyboardInputs(this));
		addMouseListener(new MouseInputs(this));
		addMouseMotionListener(new MouseInputs(this));
		ImportImage();

	}
	
	private void ImportImage() {
		InputStream is = getClass().getResourceAsStream("/Char.png");
		
		try {
			img= ImageIO.read(is);

		} catch (IOException e){
			e.printStackTrace();
		}
		
	}

	private void SetPanelSize() {
		Dimension size = new Dimension(900,600);
		setMinimumSize(size);
		setPreferredSize(size);

		
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
	
	@Override
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    //subImg = img.getSubimage(X, Y, [x,y dimensiones de la imagen a mostrar]); 

	    
	    subImg = img.getSubimage(2*32, 2*32, 32, 32); 
	    

	    
	    g.drawImage(subImg, (int)xDelta, (int)yDelta, 64, 64, null);

	}


	
}
