package ui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import gamestates.Gamestate;
import main.Game;
import utils.LoadSave;

/**
 * Main menu  –  menu_background.png (282×336) scaled to screen center
 *             –  main_menu_buttons.png (420×168) → 3 rows × 1 col of 140×168 each
 *                Row 0 = Play, Row 1 = Options, Row 2 = Quit
 *                (each button has 3 cols: normal / hover / pressed, 140×168 px)
 */
public class MainMenu {

    private Game game;
    private BufferedImage bgImg;
    private BufferedImage panelImg;

    // Each button strip: 3 states × 1 row → 140×168 each
    private BufferedImage[][] btnImgs;   // [buttonIndex 0-2][state 0-2]
    private static final int BTN_COLS   = 3;  // normal / hover / pressed
    private static final int BTN_ROWS   = 3;  // play / options / quit
    private static final int BTN_W_SRC  = 140;
    private static final int BTN_H_SRC  = 56;

    private static final int BTN_W      = 200;
    private static final int BTN_H      = 70;
    private static final int BTN_GAP    = 35;

    // Bounding rectangles for mouse hit-test
    private Rectangle[] btnRects;
    private int[]       btnState;   // 0=normal 1=hover 2=pressed

    private Rectangle panelRect;

    // Konami
    private int konamiIndex = 0;

    // Options sub-panel
    private OptionsMenu optionsMenu;
    private boolean showOptions = false;

    // Level select sub-panel
    private LevelSelect levelSelect;
    private boolean showLevelSelect = false;

    public MainMenu(Game game) {
        this.game      = game;
        optionsMenu    = new OptionsMenu(game);
        levelSelect    = new LevelSelect(game);
        loadImages();
        layoutButtons();
    }

    private void loadImages() {
        bgImg = LoadSave.GetSpriteAtlas(LoadSave.MENU_BACKGROUND);
        panelImg = LoadSave.GetSpriteAtlas(LoadSave.MENU_PANEL);

        
        
        BufferedImage sheet = LoadSave.GetSpriteAtlas(LoadSave.MENU_BUTTONS);
        btnImgs = new BufferedImage[BTN_ROWS][BTN_COLS];
        if (sheet != null) {
            int srcW = sheet.getWidth()  / BTN_COLS;
            int srcH = sheet.getHeight() / BTN_ROWS;
            for (int r = 0; r < BTN_ROWS; r++)
                for (int c = 0; c < BTN_COLS; c++)
                    btnImgs[r][c] = sheet.getSubimage(c * srcW, r * srcH, srcW, srcH);
        }
    }

    private void layoutButtons() {
        int cx = Game.GAME_WIDTH  / 2;
        int cy = Game.GAME_HEIGHT / 2;
        int totalH = BTN_ROWS * BTN_H + (BTN_ROWS - 1) * BTN_GAP;
        int startY = cy - totalH / 2 + 30;

        btnRects = new Rectangle[BTN_ROWS];
        btnState = new int[BTN_ROWS];
        for (int i = 0; i < BTN_ROWS; i++)
            btnRects[i] = new Rectangle(cx - BTN_W / 2, startY + i * (BTN_H + BTN_GAP), BTN_W, BTN_H);

        int panelW = 300, panelH = 280;
        panelRect = new Rectangle(cx - panelW / 2, cy - panelH / 2, panelW, panelH);
    }

    public void draw(Graphics g) {
        // Background
        if (bgImg != null)
            g.drawImage(bgImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
        else {
            g.setColor(new Color(20, 20, 40));
            g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
        }

        // Title
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.setColor(Color.WHITE);
        String title = "SHOOTER";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, Game.GAME_WIDTH / 2 - fm.stringWidth(title) / 2, 80);
        
        //Panel
        if (panelImg != null)
        	g.drawImage(panelImg, (Game.GAME_WIDTH - panelImg.getWidth()) / 2, (Game.GAME_HEIGHT - panelImg.getHeight())/2,  400, 500, null);
   
        // Buttons
        String[] labels = {"PLAY", "OPTIONS", "QUIT"};
        for (int i = 0; i < BTN_ROWS; i++) {
            if (btnImgs[i] != null && btnImgs[i][btnState[i]] != null) {
                g.drawImage(btnImgs[i][btnState[i]],
                    btnRects[i].x, btnRects[i].y, btnRects[i].width, btnRects[i].height, null);
            } else {
                // Fallback solid button
                g.setColor(btnState[i] == 2 ? new Color(80,80,160)
                         : btnState[i] == 1 ? new Color(100,100,200)
                         :                    new Color(60, 60,130));
                g.fillRoundRect(btnRects[i].x, btnRects[i].y, btnRects[i].width, btnRects[i].height, 10, 10);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 18));
                fm = g.getFontMetrics();
                g.drawString(labels[i],
                    btnRects[i].x + (btnRects[i].width - fm.stringWidth(labels[i])) / 2,
                    btnRects[i].y + (btnRects[i].height + fm.getAscent()) / 2 - 2);
            }
        }

        if (showOptions)    optionsMenu.draw(g);
        if (showLevelSelect) levelSelect.draw(g);
    }

    public void update() {
        if (showOptions)    optionsMenu.update();
        if (showLevelSelect) levelSelect.update();
    }

    // ── Input ──────────────────────────────────────────────────────────────────

    public void mousePressed(MouseEvent e) {
        if (showOptions)    { optionsMenu.mousePressed(e); return; }
        if (showLevelSelect){ levelSelect.mousePressed(e); return; }
        for (int i = 0; i < BTN_ROWS; i++)
            if (btnRects[i].contains(e.getPoint())) btnState[i] = 2;
    }

    public void mouseReleased(MouseEvent e) {
        if (showOptions)    { optionsMenu.mouseReleased(e); return; }
        if (showLevelSelect){ levelSelect.mouseReleased(e); return; }
        for (int i = 0; i < BTN_ROWS; i++) {
            if (btnRects[i].contains(e.getPoint()) && btnState[i] == 2) {
                handleButton(i);
            }
            btnState[i] = 0;
        }
    }

    public void mouseMoved(MouseEvent e) {
        if (showOptions)    { optionsMenu.mouseMoved(e); return; }
        if (showLevelSelect){ levelSelect.mouseMoved(e); return; }
        for (int i = 0; i < BTN_ROWS; i++)
            btnState[i] = btnRects[i].contains(e.getPoint()) ? 1 : 0;
    }

    public void mouseDragged(MouseEvent e) {
        if (showOptions)    { optionsMenu.mouseDragged(e); return; }
        //if (showLevelSelect){ levelSelect.mouseDragged(e); return; }
    }

    private void handleButton(int i) {
        switch (i) {
            case 0: showLevelSelect = true;  break; // Play → level select
            case 1: showOptions     = true;  break; // Options
            case 2: System.exit(0);                  // Quit
        }
    }

    public void keyPressed(int keyCode) {
        if (showOptions)    { optionsMenu.keyPressed(keyCode); return; }
        if (showLevelSelect){ levelSelect.keyPressed(keyCode); return; }

        // Konami code
        if (keyCode == utils.Constants.KONAMI_CODE[konamiIndex]) {
            konamiIndex++;
            if (konamiIndex == utils.Constants.KONAMI_CODE.length) {
                konamiIndex = 0;
                game.setGamestate(Gamestate.KONAMI);
            }
        } else {
            konamiIndex = 0;
        }
    }

    // Called from sub-menus to close themselves
    public void closeOptions()    { showOptions     = false; }
    public void closeLevelSelect(){ showLevelSelect = false; }

    public OptionsMenu getOptionsMenu()  { return optionsMenu; }
    public LevelSelect getLevelSelect()  { return levelSelect; }
}
