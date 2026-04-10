package ui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import gamestates.Gamestate;
import main.Game;
import utils.LoadSave;

/**
 * Pause menu overlay.
 * pause_menu.png (258×389) = panel background
 * pause_menu_buttons.png (168×168) = 3×3 grid of 56×56 buttons
 *   Row 0: Resume, Restart, Quit
 *   Row 1: Sound off, Sound on  (cols 0-1)
 *   Row 2: Music off, Music on  (cols 0-1)
 * volume_slider.png (299×44) = slider graphic
 */
public class PauseMenu {

    private Game game;

    private BufferedImage panelImg;
    private BufferedImage[][] pauseButtons; // [row][col] 3x3 at 56x56
    private BufferedImage sliderImg;

    private Rectangle resumeBtn, restartBtn, quitBtn;
    private int resumeState, restartState, quitState;

    private Rectangle soundBtn, musicBtn;
    private int soundBtnState, musicBtnState;

    private Rectangle sliderTrack, sliderThumb;
    private boolean dragging = false;

    private Rectangle panel;

    public PauseMenu(Game game) {
        this.game = game;
        loadImages();
        layout();
    }

    private void loadImages() {
        panelImg    = LoadSave.GetSpriteAtlas(LoadSave.PAUSE_MENU);
        sliderImg   = LoadSave.GetSpriteAtlas(LoadSave.VOLUME_SLIDER);
        BufferedImage sheet = LoadSave.GetSpriteAtlas(LoadSave.PAUSE_BUTTONS);
        pauseButtons = new BufferedImage[3][3];
        if (sheet != null) {
            int cw = sheet.getWidth()/3, ch = sheet.getHeight()/3;
            for (int r=0;r<3;r++)
                for (int c=0;c<3;c++)
                    pauseButtons[r][c] = sheet.getSubimage(c*cw, r*ch, cw, ch);
        }
    }

    private void layout() {
        int pw = 300, ph = 380;
        int px = Game.GAME_WIDTH/2 - pw/2;
        int py = Game.GAME_HEIGHT/2 - ph/2;
        panel = new Rectangle(px, py, pw, ph);

        int btnW = 120, btnH = 40, gap = 12;
        int bx = px + (pw - btnW) / 2;
        int by = py + 70;

        resumeBtn  = new Rectangle(bx, by,           btnW, btnH);
        restartBtn = new Rectangle(bx, by+btnH+gap,  btnW, btnH);
        quitBtn    = new Rectangle(bx, by+2*(btnH+gap), btnW, btnH);

        // Sound & Music side by side
        int tbW = 110, tbH = 38;
        soundBtn = new Rectangle(px+20,           by+3*(btnH+gap)+10, tbW, tbH);
        musicBtn = new Rectangle(px+pw-20-tbW,   by+3*(btnH+gap)+10, tbW, tbH);

        // Volume slider below toggles
        int slW = pw-40, slH = 16;
        sliderTrack = new Rectangle(px+20, by+3*(btnH+gap)+10+tbH+20, slW, slH);
        updateThumb();
    }

    private void updateThumb() {
        float vol = game.getAudioManager().getMasterVolume();
        int tx = sliderTrack.x + (int)(vol * sliderTrack.width) - 8;
        sliderThumb = new Rectangle(tx, sliderTrack.y-5, 16, sliderTrack.height+10);
    }

    public void draw(Graphics g) {
        // Semi-transparent overlay
        g.setColor(new Color(0,0,0,130));
        g.fillRect(0,0,Game.GAME_WIDTH,Game.GAME_HEIGHT);

        // Panel bg
        if (panelImg != null)
            g.drawImage(panelImg, panel.x, panel.y, panel.width, panel.height, null);
        else {
            g.setColor(new Color(20,20,50,230));
            g.fillRoundRect(panel.x,panel.y,panel.width,panel.height,16,16);
            g.setColor(Color.WHITE);
            g.drawRoundRect(panel.x,panel.y,panel.width,panel.height,16,16);
        }

        // Title
        g.setFont(new Font("Arial",Font.BOLD,22));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        String title = "PAUSED";
        g.drawString(title, Game.GAME_WIDTH/2 - fm.stringWidth(title)/2, panel.y+45);

        // Buttons
        drawBtn(g, resumeBtn,  resumeState,  "RESUME");
        drawBtn(g, restartBtn, restartState, "RESTART");
        drawBtn(g, quitBtn,    quitState,    "QUIT");

        // Sound toggle
        boolean sfx = game.getAudioManager().isSoundEnabled();
        drawToggle(g, soundBtn, soundBtnState, "SFX: "+(sfx?"ON":"OFF"), sfx);

        // Music toggle
        boolean bgm = game.getAudioManager().isMusicEnabled();
        drawToggle(g, musicBtn, musicBtnState, "BGM: "+(bgm?"ON":"OFF"), bgm);

        // Volume label + slider
        g.setFont(new Font("Arial",Font.PLAIN,13));
        g.setColor(Color.WHITE);
        g.drawString("Volume", sliderTrack.x, sliderTrack.y-8);

        if (sliderImg != null) {
            g.drawImage(sliderImg, sliderTrack.x, sliderTrack.y-2,
                sliderTrack.width, sliderTrack.height+4, null);
        } else {
            g.setColor(new Color(50,50,50));
            g.fillRoundRect(sliderTrack.x, sliderTrack.y, sliderTrack.width, sliderTrack.height, 8,8);
            g.setColor(new Color(100,180,255));
            int f = (int)(game.getAudioManager().getMasterVolume()*sliderTrack.width);
            g.fillRoundRect(sliderTrack.x, sliderTrack.y, f, sliderTrack.height, 8,8);
        }
        g.setColor(Color.WHITE);
        g.fillOval(sliderThumb.x, sliderThumb.y, sliderThumb.width, sliderThumb.height);
    }

    private void drawBtn(Graphics g, Rectangle r, int state, String label) {
        g.setColor(state==2?new Color(60,60,130):state==1?new Color(90,90,180):new Color(50,50,100));
        g.fillRoundRect(r.x,r.y,r.width,r.height,8,8);
        g.setColor(Color.WHITE);
        g.drawRoundRect(r.x,r.y,r.width,r.height,8,8);
        g.setFont(new Font("Arial",Font.BOLD,15));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(label, r.x+(r.width-fm.stringWidth(label))/2,
                            r.y+(r.height+fm.getAscent())/2-2);
    }

    private void drawToggle(Graphics g, Rectangle r, int state, String label, boolean on) {
        g.setColor(state==2?(on?new Color(0,120,0):new Color(120,0,0))
                 : state==1?(on?new Color(0,160,0):new Color(160,0,0))
                 :           (on?new Color(0,100,0):new Color(100,0,0)));
        g.fillRoundRect(r.x,r.y,r.width,r.height,8,8);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial",Font.BOLD,13));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(label, r.x+(r.width-fm.stringWidth(label))/2,
                            r.y+(r.height+fm.getAscent())/2-2);
    }

    public void mousePressed(MouseEvent e) {
        if (resumeBtn .contains(e.getPoint())) resumeState   = 2;
        if (restartBtn.contains(e.getPoint())) restartState  = 2;
        if (quitBtn   .contains(e.getPoint())) quitState     = 2;
        if (soundBtn  .contains(e.getPoint())) soundBtnState = 2;
        if (musicBtn  .contains(e.getPoint())) musicBtnState = 2;
        if (sliderThumb.contains(e.getPoint())||sliderTrack.contains(e.getPoint())) {
            dragging=true; updateVolume(e.getX());
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (resumeBtn .contains(e.getPoint())&&resumeState ==2) game.setGamestate(Gamestate.PLAYING);
        if (restartBtn.contains(e.getPoint())&&restartState==2) game.restartLevel();
        if (quitBtn   .contains(e.getPoint())&&quitState   ==2) game.setGamestate(Gamestate.MENU);
        if (soundBtn  .contains(e.getPoint())&&soundBtnState==2)
            game.getAudioManager().setSoundEnabled(!game.getAudioManager().isSoundEnabled());
        if (musicBtn  .contains(e.getPoint())&&musicBtnState==2)
            game.getAudioManager().setMusicEnabled(!game.getAudioManager().isMusicEnabled());
        resumeState=restartState=quitState=soundBtnState=musicBtnState=0;
        dragging=false;
    }

    public void mouseMoved(MouseEvent e) {
        resumeState   = resumeBtn .contains(e.getPoint())?1:0;
        restartState  = restartBtn.contains(e.getPoint())?1:0;
        quitState     = quitBtn   .contains(e.getPoint())?1:0;
        soundBtnState = soundBtn  .contains(e.getPoint())?1:0;
        musicBtnState = musicBtn  .contains(e.getPoint())?1:0;
    }

    public void mouseDragged(MouseEvent e) { if(dragging) updateVolume(e.getX()); }

    private void updateVolume(int mx) {
        float vol = (float)(mx-sliderTrack.x)/sliderTrack.width;
        game.getAudioManager().setMasterVolume(vol);
        updateThumb();
    }
}
