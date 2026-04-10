package inputs;

import java.awt.event.*;
import main.GamePanel;

public class MouseInputs implements MouseListener, MouseMotionListener {

    private GamePanel panel;

    public MouseInputs(GamePanel panel) { this.panel = panel; }

    @Override public void mousePressed (MouseEvent e) { panel.getGame().mousePressed(e); }
    @Override public void mouseReleased(MouseEvent e) { panel.getGame().mouseReleased(e); }
    @Override public void mouseMoved   (MouseEvent e) { panel.getGame().mouseMoved(e); }
    @Override public void mouseDragged (MouseEvent e) { panel.getGame().mouseDragged(e); }

    @Override public void mouseClicked (MouseEvent e) {}
    @Override public void mouseEntered (MouseEvent e) {}
    @Override public void mouseExited  (MouseEvent e) {}
}
