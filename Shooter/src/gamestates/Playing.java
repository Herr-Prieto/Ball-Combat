package gamestates;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import entities.EnemyManager;
import entities.Player;
import levels.LevelManager;
import main.Game;
import objects.ProjectileManager;
import ui.PauseMenu;
import utils.LoadSave;

public class Playing {

    private Game game;
    private LevelManager levelManager;
    private Player player;
    private EnemyManager enemyManager;
    private ProjectileManager projectileManager;
    private PauseMenu pauseMenu;

    private BufferedImage background;

    // Victory / Game Over timers
    private boolean victory  = false;
    private boolean gameOver = false;
    private long    resultTime = -1;
    private static final long GAMEOVER_DELAY = 5000L;
    private static final long WIN_DELAY      = 10000L;

    public Playing(Game game, LevelManager levelManager, Player player,
                   EnemyManager enemyManager, ProjectileManager projectileManager) {
        this.game               = game;
        this.levelManager       = levelManager;
        this.player             = player;
        this.enemyManager       = enemyManager;
        this.projectileManager  = projectileManager;
        this.pauseMenu          = new PauseMenu(game);
        this.background         = LoadSave.GetSpriteAtlas(LoadSave.BACKGROUND);
    }

    public void update() {
        if (gameOver || victory) {
            long elapsed = System.currentTimeMillis() - resultTime;
            long delay   = victory ? WIN_DELAY : GAMEOVER_DELAY;
            if (elapsed >= delay) {
                game.setGamestate(Gamestate.MENU);
                reset();
            }
            return;
        }

        levelManager.update();
        player.update(projectileManager);

        // Player death
        if (!player.isAlive() && !gameOver) {
            gameOver   = true;
            resultTime = System.currentTimeMillis();
        }

        enemyManager.update(levelManager.getCurrentLevel().getLevelData(), player, projectileManager);
        projectileManager.update(levelManager.getCurrentLevel().getLevelData(),
                                  player, enemyManager.getEnemies());

        // Victory: all enemies defeated
        if (!victory && !gameOver && enemyManager.allDefeated()) {
            victory    = true;
            resultTime = System.currentTimeMillis();
        }
    }

    public void draw(Graphics g) {
        // Background
        if (background != null)
            g.drawImage(background, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
        else {
            g.setColor(new Color(40,40,80));
            g.fillRect(0,0,Game.GAME_WIDTH,Game.GAME_HEIGHT);
        }

        levelManager.draw(g);
        enemyManager.draw(g);
        projectileManager.draw(g);
        player.render(g);

        drawPelletHUD(g);

        if (gameOver) drawOverlay(g, "GAME OVER", Color.RED, GAMEOVER_DELAY);
        if (victory)  drawOverlay(g, "YOU WIN!",  Color.YELLOW, WIN_DELAY);
    }

    private void drawPelletHUD(Graphics g) {
        int sel = player.getSelectedPellet();
        String[] names = {"SHOT 1","SHOT 2","SHOT 3","SHOT 4"};
        g.setFont(new Font("Arial",Font.BOLD,13));
        int bx = Game.GAME_WIDTH - 160, by = 10;
        for (int i=0;i<4;i++) {
            if (i == sel) { g.setColor(Color.YELLOW); g.fillRoundRect(bx-2, by+i*22-2, 100, 20, 5,5); }
            g.setColor(i==sel?Color.BLACK:Color.WHITE);
            g.drawString((i+1)+": "+names[i], bx, by+i*22+14);
        }
    }

    private void drawOverlay(Graphics g, String text, Color color, long delay) {
        g.setColor(new Color(0,0,0,150));
        g.fillRect(0,0,Game.GAME_WIDTH,Game.GAME_HEIGHT);
        g.setFont(new Font("Arial",Font.BOLD,48));
        g.setColor(color);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, Game.GAME_WIDTH/2 - fm.stringWidth(text)/2, Game.GAME_HEIGHT/2-20);
        g.setFont(new Font("Arial",Font.PLAIN,18));
        g.setColor(Color.WHITE);
        long elapsed = System.currentTimeMillis() - resultTime;
        long remaining = Math.max(0, (delay - elapsed) / 1000);
        String sub = "Returning to menu in " + remaining + "s...";
        fm = g.getFontMetrics();
        g.drawString(sub, Game.GAME_WIDTH/2 - fm.stringWidth(sub)/2, Game.GAME_HEIGHT/2+30);
    }

    public void reset() {
        gameOver = false;
        victory  = false;
        resultTime = -1;
        projectileManager.clear();
    }

    public boolean isGameOver(){ return gameOver; }
    public boolean isVictory() { return victory;  }

    public PauseMenu getPauseMenu() { return pauseMenu; }

    // ── Input forwarding ──────────────────────────────────────────────────────

    public void keyPressed(int key) {
        switch (key) {
            case java.awt.event.KeyEvent.VK_A:     player.setLeft(true);  break;
            case java.awt.event.KeyEvent.VK_D:     player.setRight(true); break;
            case java.awt.event.KeyEvent.VK_SPACE: player.setJump(true);  break;
            case java.awt.event.KeyEvent.VK_K:     player.setWantsToShoot(true); break;
            case java.awt.event.KeyEvent.VK_1:     player.setSelectedPellet(0);  break;
            case java.awt.event.KeyEvent.VK_2:     player.setSelectedPellet(1);  break;
            case java.awt.event.KeyEvent.VK_3:     player.setSelectedPellet(2);  break;
            case java.awt.event.KeyEvent.VK_4:     player.setSelectedPellet(3);  break;
            case java.awt.event.KeyEvent.VK_ESCAPE:game.setGamestate(Gamestate.PAUSED); break;
        }
    }

    public void keyReleased(int key) {
        switch (key) {
            case java.awt.event.KeyEvent.VK_A:     player.setLeft(false);  break;
            case java.awt.event.KeyEvent.VK_D:     player.setRight(false); break;
            case java.awt.event.KeyEvent.VK_SPACE: player.setJump(false);  break;
            case java.awt.event.KeyEvent.VK_K:     player.setWantsToShoot(false); break;
        }
    }
}
