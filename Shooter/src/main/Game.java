package main;

import java.awt.Graphics;

import audio.AudioManager;
import entities.EnemyManager;
import entities.Player;
import gamestates.Gamestate;
import gamestates.Playing;
import levels.LevelManager;
import objects.ProjectileManager;
import ui.KonamiScreen;
import ui.MainMenu;

public class Game implements Runnable {

    private GameWindow  gameWindow;
    private GamePanel   gamePanel;
    private Thread      gameThread;

    private final int FPS_SET = 120;
    private final int UPS_SET = 200;

    // Constants
    public final static int TILES_DEFAULT_SIZE = 32;
    public final static float SCALE            = 1.5f;
    public final static int TILES_IN_WIDTH     = 26;
    public final static int TILES_IN_HEIGHT    = 14;
    public final static int TILES_SIZE         = (int)(TILES_DEFAULT_SIZE * SCALE);
    public final static int GAME_WIDTH         = TILES_SIZE * TILES_IN_WIDTH;
    public final static int GAME_HEIGHT        = TILES_SIZE * TILES_IN_HEIGHT;

    // Core systems
    private AudioManager     audioManager;
    private LevelManager     levelManager;
    private Player           player;
    private EnemyManager     enemyManager;
    private ProjectileManager projectileManager;

    // Game states / UI
    private Gamestate  gamestate = Gamestate.MENU;
    private Playing    playing;
    private MainMenu   mainMenu;
    private KonamiScreen konamiScreen;

    public Game() {
        initSystems();
        gamePanel  = new GamePanel(this);
        gameWindow = new GameWindow(gamePanel);
        gamePanel.requestFocus();
        startGameLoop();
    }

    private void initSystems() {
        audioManager     = new AudioManager();
        levelManager     = new LevelManager(this);
        player           = new Player(200, 200, (int)(64*SCALE), (int)(64*SCALE));
        enemyManager     = new EnemyManager(this);
        projectileManager = new ProjectileManager(this);
        playing          = new Playing(this, levelManager, player, enemyManager, projectileManager);
        mainMenu         = new MainMenu(this);
        konamiScreen     = new KonamiScreen(this);
    }

    // ── Level lifecycle ────────────────────────────────────────────────────────

    public void startLevel(int index) {
        levelManager.setLevel(index);
        levels.Level level = levelManager.getCurrentLevel();

        // Spawn player
        int[] ps = level.getPlayerSpawn();
        player.respawn(ps[0] * TILES_SIZE, ps[1] * TILES_SIZE);
        player.loadlvlData(level.getLevelData());

        // Spawn enemies
        enemyManager.loadEnemies(level);
        projectileManager.clear();
        playing.reset();

        setGamestate(Gamestate.PLAYING);
        audioManager.playMusic();
    }

    public void restartLevel() {
        startLevel(levelManager.getCurrentLevelIndex());
    }

    // ── Game loop ─────────────────────────────────────────────────────────────

    private void startGameLoop() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double timePerFrame  = 1_000_000_000.0 / FPS_SET;
        double timePerUpdate = 1_000_000_000.0 / UPS_SET;
        long   prevTime      = System.nanoTime();
        double deltaU = 0, deltaF = 0;
        int frames = 0, updates = 0;
        long lastCheck = System.currentTimeMillis();

        while (true) {
            long now = System.nanoTime();
            deltaU += (now - prevTime) / timePerUpdate;
            deltaF += (now - prevTime) / timePerFrame;
            prevTime = now;

            if (deltaU >= 1) { update(); updates++; deltaU--; }
            if (deltaF >= 1) { gamePanel.repaint(); frames++; deltaF--; }

            if (System.currentTimeMillis() - lastCheck >= 1000) {
                lastCheck = System.currentTimeMillis();
                System.out.println("FPS: " + frames + " | UPS: " + updates);
                frames = 0; updates = 0;
            }
        }
    }

    public void update() {
        switch (gamestate) {
            case MENU:   mainMenu.update();  break;
            case PLAYING:playing.update();   break;
            case PAUSED: /* no physics */    break;
            case KONAMI: /* static */        break;
        }
    }

    public void render(Graphics g) {
        switch (gamestate) {
            case MENU:
                mainMenu.draw(g);
                break;
            case PLAYING:
                playing.draw(g);
                break;
            case PAUSED:
                playing.draw(g);
                playing.getPauseMenu().draw(g);
                break;
            case KONAMI:
                konamiScreen.draw(g);
                break;
        }
    }

    // ── Input routing ─────────────────────────────────────────────────────────

    public void keyPressed(int key) {
        switch (gamestate) {
            case MENU:   mainMenu.keyPressed(key);    break;
            case PLAYING:playing.keyPressed(key);     break;
            case KONAMI: konamiScreen.keyPressed(key);break;
            case PAUSED:
                if (key == java.awt.event.KeyEvent.VK_ESCAPE)
                    setGamestate(Gamestate.PLAYING);
                break;
        }
    }

    public void keyReleased(int key) {
        if (gamestate == Gamestate.PLAYING) playing.keyReleased(key);
    }

    public void mousePressed(java.awt.event.MouseEvent e) {
        switch (gamestate) {
            case MENU:   mainMenu.mousePressed(e);         break;
            case PAUSED: playing.getPauseMenu().mousePressed(e); break;
            case KONAMI: konamiScreen.mousePressed(e);     break;
        }
    }

    public void mouseReleased(java.awt.event.MouseEvent e) {
        switch (gamestate) {
            case MENU:   mainMenu.mouseReleased(e);         break;
            case PAUSED: playing.getPauseMenu().mouseReleased(e); break;
        }
    }

    public void mouseMoved(java.awt.event.MouseEvent e) {
        switch (gamestate) {
            case MENU:   mainMenu.mouseMoved(e); break;
            case PAUSED: playing.getPauseMenu().mouseMoved(e); break;
        }
    }

    public void mouseDragged(java.awt.event.MouseEvent e) {
        switch (gamestate) {
            case MENU:   mainMenu.mouseDragged(e); break;
            case PAUSED: playing.getPauseMenu().mouseDragged(e); break;
        }
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public Gamestate      getGamestate()     { return gamestate; }
    public AudioManager   getAudioManager()  { return audioManager; }
    public Player         getPlayer()        { return player; }
    public MainMenu       getMainMenu()      { return mainMenu; }
    public LevelManager   getLevelManager()  { return levelManager; }

    public void setGamestate(Gamestate gs) {
        if (gs == Gamestate.MENU) audioManager.stopMusic();
        gamestate = gs;
    }

    public void windowFocusLost() {
        if (gamestate == Gamestate.PLAYING)
            setGamestate(Gamestate.PAUSED);
    }
}
