package ui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import gamestates.Gamestate;
import main.Game;
import utils.LoadSave;

public class LevelSelect {

    private Game game;
    private Rectangle[] levelBtns;
    private int[] btnState;
    private Rectangle backBtn;
    private int backState;

    public LevelSelect(Game game) {
        this.game = game;
        int cx = Game.GAME_WIDTH / 2;
        int cy = Game.GAME_HEIGHT / 2;
        levelBtns = new Rectangle[]{
            new Rectangle(cx - 160, cy - 40, 140, 60),
            new Rectangle(cx + 20,  cy - 40, 140, 60)
        };
        btnState  = new int[2];
        backBtn   = new Rectangle(cx - 60, cy + 60, 120, 40);
    }

    public void draw(Graphics g) {
        // Dim overlay
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        // Panel
        int pw = 380, ph = 220;
        int px = Game.GAME_WIDTH / 2 - pw / 2;
        int py = Game.GAME_HEIGHT / 2 - ph / 2;
        g.setColor(new Color(30, 30, 60, 240));
        g.fillRoundRect(px, py, pw, ph, 20, 20);
        g.setColor(Color.WHITE);
        g.drawRoundRect(px, py, pw, ph, 20, 20);

        // Title
        g.setFont(new Font("Arial", Font.BOLD, 22));
        FontMetrics fm = g.getFontMetrics();
        String title = "SELECT LEVEL";
        g.drawString(title, Game.GAME_WIDTH/2 - fm.stringWidth(title)/2, py + 40);

        // Level buttons
        String[] labels = {"LEVEL  1", "LEVEL  2"};
        for (int i = 0; i < 2; i++) {
            Color base = (i == 0) ? new Color(40,100,180) : new Color(140,60,40);
            g.setColor(btnState[i] == 2 ? base.darker()
                     : btnState[i] == 1 ? base.brighter()
                     :                    base);
            g.fillRoundRect(levelBtns[i].x, levelBtns[i].y,
                            levelBtns[i].width, levelBtns[i].height, 10, 10);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            fm = g.getFontMetrics();
            g.drawString(labels[i],
                levelBtns[i].x + (levelBtns[i].width  - fm.stringWidth(labels[i])) / 2,
                levelBtns[i].y + (levelBtns[i].height + fm.getAscent()) / 2 - 2);
        }

        // Back
        g.setColor(backState == 2 ? Color.GRAY : backState == 1 ? Color.LIGHT_GRAY : new Color(80,80,80));
        g.fillRoundRect(backBtn.x, backBtn.y, backBtn.width, backBtn.height, 8, 8);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 15));
        fm = g.getFontMetrics();
        g.drawString("BACK", backBtn.x + (backBtn.width - fm.stringWidth("BACK"))/2,
                             backBtn.y + (backBtn.height + fm.getAscent())/2 - 2);
    }

    public void update() {}

    public void mousePressed(MouseEvent e) {
        for (int i = 0; i < 2; i++)
            if (levelBtns[i].contains(e.getPoint())) btnState[i] = 2;
        if (backBtn.contains(e.getPoint())) backState = 2;
    }

    public void mouseReleased(MouseEvent e) {
        for (int i = 0; i < 2; i++) {
            if (levelBtns[i].contains(e.getPoint()) && btnState[i] == 2) {
                game.startLevel(i);
            }
            btnState[i] = 0;
        }
        if (backBtn.contains(e.getPoint()) && backState == 2) {
            game.getMainMenu().closeLevelSelect();
        }
        backState = 0;
    }

    public void mouseMoved(MouseEvent e) {
        for (int i = 0; i < 2; i++)
            btnState[i] = levelBtns[i].contains(e.getPoint()) ? 1 : 0;
        backState = backBtn.contains(e.getPoint()) ? 1 : 0;
    }

    public void keyPressed(int keyCode) {}
}
