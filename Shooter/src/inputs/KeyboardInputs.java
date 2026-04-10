package inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import main.GamePanel;

public class KeyboardInputs implements KeyListener {

    private GamePanel panel;

    public KeyboardInputs(GamePanel panel) { this.panel = panel; }

    @Override
    public void keyPressed(KeyEvent e) {
        panel.getGame().keyPressed(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        panel.getGame().keyReleased(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
