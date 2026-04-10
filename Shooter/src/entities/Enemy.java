package entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.Game;
import objects.ProjectileManager;
import utils.Constants.EnemyConstants;
import utils.HelpMethods;
import utils.LoadSave;

public class Enemy extends Entity {

    // Spritesheet: Enemy_01.png 384x192 = 12 cols x 6 rows, 32x32
    private static BufferedImage[][] animations;
    private static boolean imagesLoaded = false;

    private int animState;
    private int animTick, animIndex;
    private static final int ANIM_SPEED = 60;

    private boolean facingRight = true;
    private float patrolSpeed   = 0.6f;

    // Physics
    private float airSpeed  = 0f;
    private float gravity   = 0.04f * Game.SCALE;
    private boolean inAir   = false;

    // AI
    private int attackCooldown    = 0;
    private static final int ATTACK_INTERVAL = 180; // updates between shots
    private boolean attacking = false;
    private boolean dying     = false;
    private int deathTimer    = 0;
    private static final int DEATH_DURATION = 150;

    // Draw offsets (sprite is 32x32, scaled)
    private static final int DRAW_SIZE = (int)(32 * Game.SCALE);

    public Enemy(float x, float y) {
        super(x, y, DRAW_SIZE, DRAW_SIZE);
        hp    = EnemyConstants.MAX_HP;
        maxHp = EnemyConstants.MAX_HP;
        animState = EnemyConstants.IDLE_RIGHT;
        loadImages();
        float hbW = 20 * Game.SCALE;
        float hbH = 28 * Game.SCALE;
        initializeHitbox(x, y, hbW, hbH);
    }

    private static synchronized void loadImages() {
        if (imagesLoaded) return;
        imagesLoaded = true;
        BufferedImage sheet = LoadSave.GetSpriteAtlas(LoadSave.ENEMY_ATLAS);
        if (sheet == null) return;
        animations = new BufferedImage[EnemyConstants.SPRITE_ROWS][EnemyConstants.SPRITE_COLS];
        for (int row = 0; row < EnemyConstants.SPRITE_ROWS; row++)
            for (int col = 0; col < EnemyConstants.SPRITE_COLS; col++)
                animations[row][col] = sheet.getSubimage(
                    col * EnemyConstants.SPRITE_SIZE,
                    row * EnemyConstants.SPRITE_SIZE,
                    EnemyConstants.SPRITE_SIZE, EnemyConstants.SPRITE_SIZE);
    }

    public void update(int[][] lvlData, Player player, ProjectileManager pm) {
        if (!alive) return;

        if (dying) {
            updateDeath();
            return;
        }

        applyGravity(lvlData);
        updateAI(lvlData, player, pm);
        updateAnimation();
    }

    private void applyGravity(int[][] lvlData) {
        if (!HelpMethods.IsEntityOnFloor(hitbox, lvlData)) {
            inAir = true;
        }
        if (inAir) {
            if (HelpMethods.CanMoveHere(hitbox.x, hitbox.y + airSpeed,
                    hitbox.width, hitbox.height, lvlData)) {
                hitbox.y += airSpeed;
                airSpeed  += gravity;
            } else {
                if (airSpeed > 0) {
                    // Landing
                    hitbox.y = HelpMethods.GetEntityYPosAboveFloor(hitbox, airSpeed);
                    inAir    = false;
                    airSpeed = 0;
                } else {
                    airSpeed = 0.5f * Game.SCALE;
                }
            }
        }
    }

    private void updateAI(int[][] lvlData, Player player, ProjectileManager pm) {
        float distX = Math.abs(player.getHitbox().x - hitbox.x);
        float detectRange = EnemyConstants.DETECTION_RANGE_TILES * Game.TILES_SIZE;

        boolean playerVisible = distX <= detectRange
            && Math.abs(player.getHitbox().y - hitbox.y) < Game.TILES_SIZE * 2;

        if (playerVisible) {
            // Face player
            facingRight = player.getHitbox().x > hitbox.x;
            attacking = true;

            // Attack cooldown
            if (attackCooldown <= 0) {
                fireAtPlayer(player, pm);
                attackCooldown = ATTACK_INTERVAL;
            } else {
                attackCooldown--;
            }
        } else {
            attacking = false;
            attackCooldown = Math.max(0, attackCooldown - 1);
            patrol(lvlData);
        }
    }

    private void patrol(int[][] lvlData) {
        float speed = facingRight ? patrolSpeed : -patrolSpeed;

        // Check wall or platform edge
        boolean wall = !HelpMethods.CanMoveHere(hitbox.x + speed, hitbox.y,
                hitbox.width, hitbox.height, lvlData);
        boolean edge = !HelpMethods.IsFloorTileBelow(hitbox, lvlData, speed);

        if (wall || edge) {
            facingRight = !facingRight;
            speed = facingRight ? patrolSpeed : -patrolSpeed;
        }

        if (HelpMethods.CanMoveHere(hitbox.x + speed, hitbox.y,
                hitbox.width, hitbox.height, lvlData)) {
            hitbox.x += speed;
        }
    }

    private void fireAtPlayer(Player player, ProjectileManager pm) {
        float projX = facingRight ? hitbox.x + hitbox.width : hitbox.x - 16 * Game.SCALE;
        float projY  = hitbox.y + hitbox.height / 2f;
        pm.addEnemyProjectile(projX, projY, facingRight);
    }

    private void updateDeath() {
        animState = facingRight ? EnemyConstants.DEAD_RIGHT : EnemyConstants.DEAD_LEFT;
        updateAnimation();
        deathTimer++;
        if (deathTimer >= DEATH_DURATION) alive = false;
    }

    private void updateAnimation() {
        if (!dying) {
            if (attacking)
                animState = facingRight ? EnemyConstants.ATTACK_RIGHT : EnemyConstants.ATTACK_LEFT;
            else
                animState = facingRight ? EnemyConstants.IDLE_RIGHT : EnemyConstants.IDLE_LEFT;
        }

        animTick++;
        if (animTick >= ANIM_SPEED) {
            animTick = 0;
            animIndex++;
            if (animIndex >= EnemyConstants.GetSpriteAmount(animState))
                animIndex = 0;
        }
    }

    public void draw(Graphics g) {
        if (!alive || animations == null) return;
        int col = Math.min(animIndex, EnemyConstants.SPRITE_COLS - 1);
        if (animations[animState][col] != null)
            g.drawImage(animations[animState][col],
                (int)(hitbox.x - 6 * Game.SCALE),
                (int)(hitbox.y - 2 * Game.SCALE),
                DRAW_SIZE, DRAW_SIZE, null);
    }

    @Override
    public void takeDamage(int dmg) {
        super.takeDamage(dmg);
        if (!alive && !dying) {
            dying = true;
            deathTimer = 0;
            alive = true; // keep alive until death animation ends
        }
    }

    public boolean isDying() { return dying; }
}
