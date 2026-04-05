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



import static utils.Constants.PlayerConstants.*;
import static utils.Constants.Directions.*;


public class GamePanel extends JPanel{
	
	private MouseInputs MouseInputs;
	private float xDelta = 100, yDelta = 100;
	
	
	private BufferedImage img;
	private BufferedImage[][]Player_Animations;
	private int animationTick, animationIndex, animationSpeed = 20;
	
	
	private int playerAction = IDLE;
	private int playerDirection = -1;
	private boolean moving = false;
	
	
	


	public GamePanel() {
		MouseInputs = new MouseInputs(this);
		SetPanelSize();
		addKeyListener(new KeyboardInputs(this));
		addMouseListener(new MouseInputs(this));
		addMouseMotionListener(new MouseInputs(this));
		
		ImportImage();
		loadAnimations();

	}
	
	private void loadAnimations() {
		Player_Animations = new BufferedImage[7][4];
		
		for(int j = 0; j < Player_Animations.length; j++) {		
			
			for(int i = 0; i < Player_Animations[j].length; i++) {
				Player_Animations[j][i] = img.getSubimage(i*32, j*32, 32, 32);
		
			}
		}
		
	}

	private void ImportImage() {
		InputStream is = getClass().getResourceAsStream("/Player_01.png");
		
		try {
			img= ImageIO.read(is);

		} catch (IOException e){
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	private void SetPanelSize() {
		Dimension size = new Dimension(900,600);
		setMinimumSize(size);
		setPreferredSize(size);

		
	}

	public void setDirection(int direction) {
		this.playerDirection = direction;
		moving = true;
			
	}
	
	public void setMoving(boolean moving) {
		this.moving = moving;
	}
	

	@Override
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    //subImg = img.getSubimage(X, Y, [x,y dimensiones de la imagen a mostrar]); 
	    updateAnimationTick();
	    
	    setAnimation();
	    updatePosition();
	    
	    
	    
	    g.drawImage(Player_Animations[playerAction][animationIndex], (int)xDelta, (int)yDelta, 100, 100, null);

	}

	private void updatePosition() {
		
		if(moving) {
			switch(playerDirection){
			case LEFT:
				xDelta-=5;
				break;
			case UP:
				yDelta-=5;
				break;
			case RIGHT:
				xDelta+=5;
				break;
			case DOWN:
				yDelta+=5;
				break;
			
			}
		}
	}

	private void setAnimation() {
		
		if(moving) {
			playerAction = MOVING;
		} else {
			playerAction = IDLE;
		}
	}

	private void updateAnimationTick() {
		animationTick++;
		if(animationTick >= animationSpeed) {
			animationTick = 0;
			animationIndex++;
			if(animationIndex >= GetSpriteAmount(playerAction)) {
				animationIndex = 0;
			}
		}
	}


	
}
