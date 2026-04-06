package inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import main.GamePanel;

public class KeyboardInputs implements KeyListener {

	private GamePanel GamePanel;

	public KeyboardInputs(GamePanel GamePanel) {
		this.GamePanel = GamePanel;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// Sin uso por ahora
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {

		// --- Movimiento horizontal ---
		case KeyEvent.VK_A:
			// Mover izquierda: la direccion (facingRight=false) la registra Player
			GamePanel.getGame().getPlayer().setLeft(true);
			break;

		case KeyEvent.VK_D:
			// Mover derecha: la direccion (facingRight=true) la registra Player
			GamePanel.getGame().getPlayer().setRight(true);
			break;

		// --- Movimiento vertical ---
		case KeyEvent.VK_W:
			GamePanel.getGame().getPlayer().setUp(true);
			break;

		case KeyEvent.VK_S:
			GamePanel.getGame().getPlayer().setDown(true);
			break;

		// --- Salto (SPACE) ---
		// SPACE activa JUMPING, que usa las filas 1 (derecha) o 3 (izquierda)
		// segun la ultima direccion registrada en Player.facingRight
		case KeyEvent.VK_SPACE:
			GamePanel.getGame().getPlayer().setJumping(true);
			break;
		case KeyEvent.VK_K:
			GamePanel.getGame().getPlayer().setAttacking(true);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {

		case KeyEvent.VK_A:
			GamePanel.getGame().getPlayer().setLeft(false);
			break;

		case KeyEvent.VK_D:
			GamePanel.getGame().getPlayer().setRight(false);
			break;

		case KeyEvent.VK_W:
			GamePanel.getGame().getPlayer().setUp(false);
			break;

		case KeyEvent.VK_S:
			GamePanel.getGame().getPlayer().setDown(false);
			break;


		case KeyEvent.VK_SPACE:
			break;
		case KeyEvent.VK_K:
			GamePanel.getGame().getPlayer().setAttacking(true);

		}
	}
}
