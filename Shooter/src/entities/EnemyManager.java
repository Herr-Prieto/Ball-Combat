package entities;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import levels.Level;
import main.Game;
import objects.ProjectileManager;

public class EnemyManager {

    private Game game;
    private List<Enemy> enemies = new ArrayList<>();

    public EnemyManager(Game game) { this.game = game; }

    public void loadEnemies(Level level) {
        enemies.clear();
        for (int[] spawn : level.getEnemySpawns()) {
            float x = spawn[0] * Game.TILES_SIZE;
            float y = spawn[1] * Game.TILES_SIZE;
            enemies.add(new Enemy(x, y));
        }
    }

    public void update(int[][] lvlData, Player player, ProjectileManager pm) {
        for (Enemy e : enemies)
            if (e.isAlive() || e.isDying())
                e.update(lvlData, player, pm);
    }

    public void draw(Graphics g) {
        for (Enemy e : enemies)
            if (e.isAlive() || e.isDying())
                e.draw(g);
    }

    public boolean allDefeated() {
        for (Enemy e : enemies)
            if (e.isAlive()) return false;
        return true;
    }

    public List<Enemy> getEnemies() { return enemies; }
}
