package ui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import gamestates.Gamestate;
import main.Game;
import utils.LoadSave;

public class KonamiScreen {

    private Game game;
    private BufferedImage img;

    public KonamiScreen(Game game) {
        this.game = game;
        img = LoadSave.GetSpriteAtlas(LoadSave.OMNI_GEIDA);
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0,0,Game.GAME_WIDTH,Game.GAME_HEIGHT);

        if (img != null) {
            // Center the image, keep aspect ratio
            double scale = Math.min((double)Game.GAME_WIDTH/img.getWidth(),
                                    (double)Game.GAME_HEIGHT/img.getHeight());
            int dw = (int)(img.getWidth()  * scale);
            int dh = (int)(img.getHeight() * scale);
            int dx = (Game.GAME_WIDTH  - dw) / 2;
            int dy = (Game.GAME_HEIGHT - dh) / 2;
            g.drawImage(img, dx, dy, dw, dh, null);
        }

        g.setColor(new Color(255,255,255,180));
        g.setFont(new Font("Arial",Font.BOLD,16));
        String msg = "Press any key to return";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(msg, Game.GAME_WIDTH/2 - fm.stringWidth(msg)/2, Game.GAME_HEIGHT - 20);
    }

    public void keyPressed(int keyCode) {
        game.setGamestate(Gamestate.MENU);
    }

    public void mousePressed(MouseEvent e) {
        game.setGamestate(Gamestate.MENU);
    }
}
