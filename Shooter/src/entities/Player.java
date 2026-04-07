package entities;

import static utils.Constants.PlayerConstants.GetSpriteAmount;
import static utils.Constants.PlayerConstants.GetSpriteRow;
import static utils.Constants.PlayerConstants.IDLE;
import static utils.Constants.PlayerConstants.MOVING;
import static utils.Constants.PlayerConstants.ATTACKING;
import static utils.Constants.PlayerConstants.JUMPING;
import static utils.Constants.PlayerConstants.HIT;

import static utils.HelpMethods.CanMoveHere;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import main.Game;
import utils.LoadSave;

public class Player extends Entity {

	// ---------------------------------------------------------------
	//  Spritesheet:  8 filas x 5 columnas de 32x32 px
	//   [fila][columna]
	//  La fila correcta se obtiene via GetSpriteRow(action, facingRight)
	// ---------------------------------------------------------------
	private BufferedImage[][] Player_Animations;

	private int animationTick, animationIndex, animationSpeed = 20;

	// Estado actual del jugador
	private int playerAction = IDLE;

	// Direcciones de movimiento (A/D/W/S)
	private boolean left, up, right, down;

	// Ultima direccion horizontal: true = derecha, false = izquierda
	// Por defecto el jugador mira a la derecha al inicio
	private boolean facingRight = true;

	private float playerSpeed = 1.0f;

	// Flags de estado
	private boolean moving    = false;
	private boolean attacking = false;
	private boolean jumping   = false;
	
	
	private int[][] lvlData;
	
	private float xDrawOffset = 6 * Game.SCALE;
	private float yDrawOffset = 0 * Game.SCALE;
	
	

	public Player(float x, float y, int width, int height) {
		super(x, y, width, height);
		loadAnimations();
		
		initializeHitbox(x, y,  30 * Game.SCALE, 36 * Game.SCALE);
	}

	public void update() {
		updatePosition();
		
		setAnimation();
		updateAnimationTick();
	}

	public void render(Graphics g) {
		// La fila del spritesheet depende de la accion Y la direccion
		int row = GetSpriteRow(playerAction, facingRight);
		g.drawImage(Player_Animations[row][animationIndex], (int)(hitbox.x - xDrawOffset), (int)(hitbox.y - yDrawOffset), 60, 55, null);
		drawHitbox(g);
	}

	// ---------------------------------------------------------------
	//  Carga del spritesheet
	//  Matriz [8][5]: 8 filas (acciones/direcciones), 5 cols maximas
	//  Las celdas que sobren para filas de menos frames no se usan.
	// ---------------------------------------------------------------
	private void loadAnimations() {
		InputStream is = getClass().getResourceAsStream("/Player_01.png");

			BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);

			// 8 filas (0-7), 5 columnas (max frames de ATTACKING)
			Player_Animations = new BufferedImage[8][5];

			for (int row = 0; row < Player_Animations.length; row++) {
				for (int col = 0; col < Player_Animations[row].length; col++) {
					Player_Animations[row][col] = img.getSubimage(col * 32, row * 32, 32, 32);
				}
			}

	}
	
	public void loadlvlData(int[][] lvlData) {
		this.lvlData = lvlData;
	}
	
	

	// ---------------------------------------------------------------
	//  Logica de animacion
	// ---------------------------------------------------------------

	private void setAnimation() {
		int previousAction = playerAction;

		// Prioridad: ATTACKING > JUMPING > MOVING > IDLE
		if (attacking) {
			playerAction = ATTACKING;
		} else if (jumping) {
			playerAction = JUMPING;
		} else if (moving) {
			playerAction = MOVING;
		} else {
			playerAction = IDLE;
		}

		// Reiniciar animacion solo si cambio de estado
		if (previousAction != playerAction) {
			resetAnimationTick();
		}
	}

	private void updateAnimationTick() {
		animationTick++;
		if (animationTick >= animationSpeed) {
			animationTick = 0;
			animationIndex++;
			if (animationIndex >= GetSpriteAmount(playerAction)) {
				animationIndex = 0;
				// Al terminar ATTACKING o JUMPING, volver al estado base
				attacking = false;
				jumping   = false;
			}
		}
	}

	private void resetAnimationTick() {
		animationTick  = 0;
		animationIndex = 0;
	}

	// ---------------------------------------------------------------
	//  Movimiento
	// ---------------------------------------------------------------

	private void updatePosition() {
		moving = false;
		if(!left && !right && !up && !down)
			return;
		
		float xSpeed = 0, ySpeed = 0;
		

		if (left && !right) {
			xSpeed -= playerSpeed;
			facingRight = false; // Actualizar direccion al moverse
		} else if (right && !left) {
			xSpeed = playerSpeed;
			facingRight = true;
		}

		if (up && !down) {
			ySpeed -= playerSpeed;
		} else if (down && !up) {
			ySpeed = playerSpeed;
		}
		/*
		if(CanMoveHere(x+xSpeed, y+ySpeed, width, height, lvlData)) {
			this.x += xSpeed;
			this.y += ySpeed; 
			moving = true;
		}
		*/
		if(CanMoveHere(hitbox.x+xSpeed, hitbox.y+ySpeed, hitbox.width, hitbox.height, lvlData)) {
			hitbox.x += xSpeed;
			hitbox.y += ySpeed; 
			moving = true;
		}
	}

	// ---------------------------------------------------------------
	//  Getters / Setters de teclas
	// ---------------------------------------------------------------

	public boolean isLeft()  { return left; }
	public void setLeft(boolean left)   { this.left  = left; }

	public boolean isUp()    { return up; }
	public void setUp(boolean up)       { this.up    = up; }

	public boolean isRight() { return right; }
	public void setRight(boolean right) { this.right = right; }

	public boolean isDown()  { return down; }
	public void setDown(boolean down)   { this.down  = down; }

	public void setAttacking(boolean attacking) { this.attacking = attacking; }
	public void setJumping(boolean jumping)     { this.jumping   = jumping; }

	/** Reinicia todas las flags de direccion (usado al perder el foco). */
	public void resetDirBooleans() {
		left  = false;
		up    = false;
		right = false;
		down  = false;
	}
}
