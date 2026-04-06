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
	private Game game;
	
	public GamePanel(Game game) {
		MouseInputs = new MouseInputs(this);
		this.game = game;
		
		
		SetPanelSize();
		addKeyListener(new KeyboardInputs(this));
		addMouseListener(new MouseInputs(this));
		addMouseMotionListener(new MouseInputs(this));
	}
	


	private void SetPanelSize() {
		Dimension size = new Dimension(900,600);
		setMinimumSize(size);
		setPreferredSize(size);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		game.render(g);
	}
	
	public Game getGame() {
		return game;
		
	}

	}
