package objects;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import entities.Enemy;
import entities.Player;
import main.Game;
import utils.Constants.ProjectileConstants;
import utils.HelpMethods;
import utils.LoadSave;

public class ProjectileManager {

    private Game game;
    private List<Projectile> projectiles = new ArrayList<>();

    // Player pellets: [type 0-3][frame 0-2]
    private BufferedImage[][] playerPelletImages;
    // Enemy pellets: [frame 0-3]
    private BufferedImage[] enemyPelletImages;

    public ProjectileManager(Game game) {
        this.game = game;
        loadImages();
    }

    private void loadImages() {
        // Player_pellets.png: 48x64 = 3 cols x 4 rows, 16x16 each
        BufferedImage pp = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_PELLETS);
        playerPelletImages = new BufferedImage[4][3];
        if (pp != null) {
            for (int row = 0; row < 4; row++)
                for (int col = 0; col < 3; col++)
                    playerPelletImages[row][col] = pp.getSubimage(col * 16, row * 16, 16, 16);
        }

        // Enemy_pellets.png: 64x16 = 4 cols x 1 row, 16x16 each
        BufferedImage ep = LoadSave.GetSpriteAtlas(LoadSave.ENEMY_PELLETS);
        enemyPelletImages = new BufferedImage[4];
        if (ep != null) {
            for (int col = 0; col < 4; col++)
                enemyPelletImages[col] = ep.getSubimage(col * 16, 0, 16, 16);
        }
    }

    public void addPlayerProjectile(float x, float y, boolean facingRight, int pelletType) {
        float speed = facingRight
            ? ProjectileConstants.PLAYER_PELLET_SPEED
            : -ProjectileConstants.PLAYER_PELLET_SPEED;
        projectiles.add(new Projectile(x, y, speed, true, pelletType));
    }

    public void addEnemyProjectile(float x, float y, boolean facingRight) {
        float speed = facingRight
            ? ProjectileConstants.ENEMY_PELLET_SPEED
            : -ProjectileConstants.ENEMY_PELLET_SPEED;
        projectiles.add(new Projectile(x, y, speed, false, 0));
    }

    public void update(int[][] lvlData, Player player, List<Enemy> enemies) {
        Iterator<Projectile> it = projectiles.iterator();
        while (it.hasNext()) {
            Projectile p = it.next();
            if (!p.isActive()) { it.remove(); continue; }

            p.update();

            // Wall collision
            if (HelpMethods.IsSolid(p.getX(), p.getY(), lvlData)
             || p.getX() < 0 || p.getX() > Game.GAME_WIDTH
             || p.getY() < 0 || p.getY() > Game.GAME_HEIGHT) {
                p.setActive(false);
                continue;
            }

            // Hit detection
            if (p.isFromPlayer()) {
                for (Enemy e : enemies) {
                    if (e.isAlive() && !e.isDying()
                     && p.getHitbox().intersects(e.getHitbox())) {
                        e.takeDamage(ProjectileConstants.PROJECTILE_DAMAGE);
                        game.getAudioManager().playShoot();
                        p.setActive(false);
                        break;
                    }
                }
            } else {
                if (player.isAlive()
                 && p.getHitbox().intersects(player.getHitbox())) {
                    player.takeDamage(ProjectileConstants.PROJECTILE_DAMAGE);
                    p.setActive(false);
                }
            }
        }
    }

    public void draw(Graphics g) {
        int displaySize = (int)(ProjectileConstants.PLAYER_PELLET_SIZE * Game.SCALE);
        for (Projectile p : projectiles) {
            if (!p.isActive()) continue;
            if (p.isFromPlayer()) {
                int type = Math.min(p.getPelletType(), 3);
                int frame = Math.min(p.getAnimIndex(), 2);
                if (playerPelletImages[type][frame] != null)
                    g.drawImage(playerPelletImages[type][frame],
                        (int) p.getX(), (int) p.getY(), displaySize, displaySize, null);
            } else {
                int frame = Math.min(p.getAnimIndex(), 3);
                if (enemyPelletImages[frame] != null)
                    g.drawImage(enemyPelletImages[frame],
                        (int) p.getX(), (int) p.getY(), displaySize, displaySize, null);
            }
        }
    }

    public void clear() { projectiles.clear(); }
}
