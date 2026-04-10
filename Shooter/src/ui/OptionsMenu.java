package ui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import main.Game;
import utils.LoadSave;

/**
 * Options panel (inside main menu):
 * - Skin selector (Player_01, 02, 03)
 * - Volume slider
 * - Sound / Music toggles
 * - Back button
 */
public class OptionsMenu {

    private Game game;

    private static final String[] SKINS = {
        LoadSave.PLAYER_ATLAS,
        LoadSave.PLAYER_ATLAS_2,
        LoadSave.PLAYER_ATLAS_3
    };
    private static final String[] SKIN_LABELS = {"SKIN 1","SKIN 2","SKIN 3"};
    private int selectedSkin = 0;

    private Rectangle[] skinBtns;
    private int[] skinState;

    // Volume slider
    private Rectangle sliderTrack;
    private Rectangle sliderThumb;
    private boolean draggingSlider = false;
    private BufferedImage sliderImg;

    // Sound / Music toggles
    private Rectangle soundBtn, musicBtn;
    private int soundState, musicState;

    // Back button
    private Rectangle backBtn;
    private int backState;

    public OptionsMenu(Game game) {
        this.game = game;
        sliderImg = LoadSave.GetSpriteAtlas(LoadSave.VOLUME_SLIDER);
        layoutComponents();
    }

    private void layoutComponents() {
        int cx = Game.GAME_WIDTH / 2;
        int cy = Game.GAME_HEIGHT / 2 - 60;

        // Skin buttons (row)
        skinBtns  = new Rectangle[3];
        skinState = new int[3];
        int skinW = 100, skinH = 40, skinGap = 10;
        int skinStartX = cx - (3*skinW + 2*skinGap)/2;
        for (int i = 0; i < 3; i++)
            skinBtns[i] = new Rectangle(skinStartX + i*(skinW+skinGap), cy + 40, skinW, skinH);

        // Volume slider
        int slW = 200, slH = 20;
        sliderTrack = new Rectangle(cx - slW/2, cy + 110, slW, slH);
        updateThumb();

        // Toggles
        soundBtn = new Rectangle(cx - 120, cy + 150, 100, 36);
        musicBtn = new Rectangle(cx + 20,  cy + 150, 100, 36);

        // Back
        backBtn = new Rectangle(cx - 60, cy + 210, 120, 36);
    }

    private void updateThumb() {
        float vol = game.getAudioManager().getMasterVolume();
        int tx = sliderTrack.x + (int)(vol * sliderTrack.width) - 8;
        sliderThumb = new Rectangle(tx, sliderTrack.y - 4, 16, sliderTrack.height + 8);
    }

    public void draw(Graphics g) {
        // Dim overlay
        g.setColor(new Color(0,0,0,170));
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        // Panel
        int pw = 380, ph = 320;
        int px = Game.GAME_WIDTH/2 - pw/2;
        int py = Game.GAME_HEIGHT/2 - ph/2 - 30;
        g.setColor(new Color(25,25,55,245));
        g.fillRoundRect(px, py, pw, ph, 18, 18);
        g.setColor(Color.WHITE);
        g.drawRoundRect(px, py, pw, ph, 18, 18);

        // Title
        g.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fm = g.getFontMetrics();
        String t = "OPTIONS";
        g.drawString(t, Game.GAME_WIDTH/2 - fm.stringWidth(t)/2, py + 38);

        // Skin label
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        fm = g.getFontMetrics();
        g.drawString("Player Skin:", px + 20, skinBtns[0].y - 8);

        // Skin buttons
        for (int i = 0; i < 3; i++) {
            boolean sel = (i == selectedSkin);
            g.setColor(sel ? new Color(80,160,80)
                    : skinState[i]==2 ? new Color(60,60,120)
                    : skinState[i]==1 ? new Color(80,80,150)
                    :                   new Color(50,50,100));
            g.fillRoundRect(skinBtns[i].x, skinBtns[i].y, skinBtns[i].width, skinBtns[i].height, 8,8);
            if (sel) { g.setColor(Color.YELLOW); g.drawRoundRect(skinBtns[i].x, skinBtns[i].y, skinBtns[i].width, skinBtns[i].height, 8,8); }
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 13));
            fm = g.getFontMetrics();
            g.drawString(SKIN_LABELS[i],
                skinBtns[i].x + (skinBtns[i].width - fm.stringWidth(SKIN_LABELS[i]))/2,
                skinBtns[i].y + (skinBtns[i].height + fm.getAscent())/2 - 2);
        }

        // Volume
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.setColor(Color.WHITE);
        g.drawString("Master Volume:", sliderTrack.x, sliderTrack.y - 8);

        if (sliderImg != null) {
            g.drawImage(sliderImg, sliderTrack.x, sliderTrack.y - 2,
                sliderTrack.width, sliderTrack.height + 4, null);
        } else {
            g.setColor(new Color(60,60,60));
            g.fillRoundRect(sliderTrack.x, sliderTrack.y, sliderTrack.width, sliderTrack.height, 8,8);
            g.setColor(new Color(100,180,255));
            int filled = (int)(game.getAudioManager().getMasterVolume() * sliderTrack.width);
            g.fillRoundRect(sliderTrack.x, sliderTrack.y, filled, sliderTrack.height, 8,8);
        }
        g.setColor(Color.WHITE);
        g.fillOval(sliderThumb.x, sliderThumb.y, sliderThumb.width, sliderThumb.height);

        // Sound / Music toggles
        drawToggle(g, soundBtn, soundState, "SFX: " + (game.getAudioManager().isSoundEnabled() ? "ON" : "OFF"));
        drawToggle(g, musicBtn, musicState, "BGM: " + (game.getAudioManager().isMusicEnabled() ? "ON" : "OFF"));

        // Back
        g.setColor(backState==2 ? Color.GRAY : backState==1 ? Color.LIGHT_GRAY : new Color(70,70,70));
        g.fillRoundRect(backBtn.x, backBtn.y, backBtn.width, backBtn.height, 8,8);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        fm = g.getFontMetrics();
        g.drawString("BACK", backBtn.x+(backBtn.width-fm.stringWidth("BACK"))/2,
                             backBtn.y+(backBtn.height+fm.getAscent())/2-2);
    }

    private void drawToggle(Graphics g, Rectangle r, int state, String label) {
        boolean on = label.contains("ON");
        g.setColor(state==2 ? (on?new Color(0,160,0):new Color(160,0,0)).darker()
                 : state==1 ? (on?new Color(0,200,0):new Color(200,0,0))
                 :             (on?new Color(0,150,0):new Color(150,0,0)));
        g.fillRoundRect(r.x, r.y, r.width, r.height, 8,8);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 13));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(label, r.x+(r.width-fm.stringWidth(label))/2,
                            r.y+(r.height+fm.getAscent())/2-2);
    }

    public void update() {}

    public void mousePressed(MouseEvent e) {
        for (int i=0;i<3;i++) if(skinBtns[i].contains(e.getPoint())) skinState[i]=2;
        if (sliderThumb.contains(e.getPoint()) || sliderTrack.contains(e.getPoint())) {
            draggingSlider = true; updateVolume(e.getX());
        }
        if (soundBtn.contains(e.getPoint())) soundState = 2;
        if (musicBtn.contains(e.getPoint())) musicState = 2;
        if (backBtn .contains(e.getPoint())) backState  = 2;
    }

    public void mouseReleased(MouseEvent e) {
        for (int i=0;i<3;i++) {
            if (skinBtns[i].contains(e.getPoint()) && skinState[i]==2) selectSkin(i);
            skinState[i]=0;
        }
        draggingSlider = false;
        if (soundBtn.contains(e.getPoint()) && soundState==2) {
            game.getAudioManager().setSoundEnabled(!game.getAudioManager().isSoundEnabled());
        }
        soundState = 0;
        if (musicBtn.contains(e.getPoint()) && musicState==2) {
            game.getAudioManager().setMusicEnabled(!game.getAudioManager().isMusicEnabled());
        }
        musicState = 0;
        if (backBtn.contains(e.getPoint()) && backState==2) {
            game.getMainMenu().closeOptions();
        }
        backState = 0;
    }

    public void mouseMoved(MouseEvent e) {
        for (int i=0;i<3;i++) skinState[i] = skinBtns[i].contains(e.getPoint())?1:0;
        soundState = soundBtn.contains(e.getPoint())?1:0;
        musicState = musicBtn.contains(e.getPoint())?1:0;
        backState  = backBtn .contains(e.getPoint())?1:0;
    }

    public void mouseDragged(MouseEvent e) {
        if (draggingSlider) updateVolume(e.getX());
    }

    private void updateVolume(int mouseX) {
        float vol = (float)(mouseX - sliderTrack.x) / sliderTrack.width;
        game.getAudioManager().setMasterVolume(vol);
        updateThumb();
    }

    private void selectSkin(int i) {
        selectedSkin = i;
        game.getPlayer().setSkin(SKINS[i]);
    }

    public void keyPressed(int keyCode) {}
}
