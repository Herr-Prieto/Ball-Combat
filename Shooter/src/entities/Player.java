package entities;

import static utils.Constants.PlayerConstants.*;
import static utils.HelpMethods.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.Game;
import objects.ProjectileManager;
import utils.LoadSave;

public class Player extends Entity {

    // Spritesheet: 5 cols x 8 rows, 32x32px
    private BufferedImage[][] Player_Animations;

    private int animationTick, animationIndex;
    private int animationSpeed = 12;

    private int playerAction  = IDLE;
    private boolean left, right, jump;
    private boolean facingRight = true;
    private float playerSpeed   = 1.5f * Game.SCALE;

    private boolean moving    = false;
    private boolean attacking = false;
    private boolean jumping   = false;

    private int[][] lvlData;

    private float xDrawOffset = 6  * Game.SCALE;
    private float yDrawOffset = 2  * Game.SCALE;

    // Physics
    private float airSpeed  = 0f;
    private float gravity   = 0.04f * Game.SCALE;
    private float jumpSpeed = -2.5f * Game.SCALE;
    private boolean inAir   = false;

    // Shooting
    private int selectedPellet = 0; // 0-3
    private int shootCooldown  = 0;
    private static final int SHOOT_INTERVAL = 25;
    private boolean wantsToShoot = false;

    // Skin
    private String currentSkin = LoadSave.PLAYER_ATLAS;

    // Death
    private boolean dead = false;
    private long deathTime = -1;

    private static final int DRAW_SIZE = (int)(32 * Game.SCALE);

    public Player(float x, float y, int width, int height) {
        super(x, y, width, height);
        hp    = MAX_HP;
        maxHp = MAX_HP;
        loadAnimations(LoadSave.PLAYER_ATLAS);
        initializeHitbox(x, y, 22 * Game.SCALE, 30 * Game.SCALE);
    }

    public void loadAnimations(String skin) {
        currentSkin = skin;
        BufferedImage img = LoadSave.GetSpriteAtlas(skin);
        if (img == null) return;
        Player_Animations = new BufferedImage[SPRITE_ROWS][SPRITE_COLS];
        for (int row = 0; row < SPRITE_ROWS; row++)
            for (int col = 0; col < SPRITE_COLS; col++)
                Player_Animations[row][col] =
                    img.getSubimage(col * SPRITE_SIZE, row * SPRITE_SIZE, SPRITE_SIZE, SPRITE_SIZE);
    }

    public void loadlvlData(int[][] lvlData) {
        this.lvlData = lvlData;
        if (!IsEntityOnFloor(hitbox, lvlData)) inAir = true;
    }

    public void update(ProjectileManager pm) {
        if (!alive) return;
        applyGravity();
        updatePosition();
        handleShooting(pm);
        setAnimation();
        updateAnimationTick();
        if (shootCooldown > 0) shootCooldown--;
    }

    // ── Gravity ────────────────────────────────────────────────────────────────

    private void applyGravity() {
        if (!IsEntityOnFloor(hitbox, lvlData)) {
            if (!inAir) { inAir = true; airSpeed = 0; }
        }
        if (inAir) {
            if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
                hitbox.y += airSpeed;
                airSpeed  += gravity;
            } else {
                if (airSpeed > 0) {
                    // Landing
                    hitbox.y = GetEntityYPosAboveFloor(hitbox, airSpeed);
                    inAir    = false;
                    airSpeed = 0;
                    jumping  = false;
                } else {
                    // Head bump
                    hitbox.y = GetEntityYPosUnderCeiling(hitbox, airSpeed);
                    airSpeed = 0.5f;
                }
            }
        }
    }

    private void updatePosition() {
        moving = false;
        float xSpeed = 0;

        if (left)  { xSpeed -= playerSpeed; facingRight = false; }
        if (right) { xSpeed += playerSpeed; facingRight = true;  }

        if (jump && !inAir) {
            inAir    = true;
            airSpeed = jumpSpeed;
            jumping  = true;
        }

        if (xSpeed != 0) {
            if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
                hitbox.x += xSpeed;
                moving = true;
            } else {
                hitbox.x = GetEntityXPosNextToWall(hitbox, xSpeed);
            }
        }
    }

    // ── Shooting ────────────────────────────────────────────────────────────────

    private void handleShooting(ProjectileManager pm) {
        if (wantsToShoot && shootCooldown <= 0) {
            float projX = facingRight
                ? hitbox.x + hitbox.width
                : hitbox.x - 16 * Game.SCALE;
            float projY = hitbox.y + hitbox.height / 2f;
            pm.addPlayerProjectile(projX, projY, facingRight, selectedPellet);
            shootCooldown = SHOOT_INTERVAL;
            wantsToShoot  = false;
            attacking     = true;
        }
    }

    // ── Animation ────────────────────────────────────────────────────────────────

    private void setAnimation() {
        int prev = playerAction;
        if      (attacking)              playerAction = ATTACKING;
        else if (jumping || inAir)       playerAction = JUMPING;
        else if (moving)                 playerAction = MOVING;
        else                             playerAction = IDLE;
        if (prev != playerAction) resetAnimationTick();
    }

    private void updateAnimationTick() {
        animationTick++;
        if (animationTick >= animationSpeed) {
            animationTick = 0;
            animationIndex++;
            if (animationIndex >= GetSpriteAmount(playerAction)) {
                animationIndex = 0;
                attacking = false;
            }
        }
    }

    private void resetAnimationTick() { animationTick = 0; animationIndex = 0; }

    // ── Render ────────────────────────────────────────────────────────────────

    public void render(Graphics g) {
        if (Player_Animations == null) return;
        int row = GetSpriteRow(playerAction, facingRight);
        int col = Math.min(animationIndex, SPRITE_COLS - 1);
        if (Player_Animations[row][col] != null)
            g.drawImage(Player_Animations[row][col],
                (int)(hitbox.x - xDrawOffset),
                (int)(hitbox.y - yDrawOffset),
                (int)(SPRITE_SIZE * Game.SCALE),
                (int)(SPRITE_SIZE * Game.SCALE), null);

        // HUD: HP bar
        drawHPBar(g);
    }

    private void drawHPBar(Graphics g) {
        int barW = 100, barH = 10;
        int bx = 10, by = 10;
        g.setColor(Color.DARK_GRAY);
        g.fillRect(bx, by, barW, barH);
        g.setColor(Color.RED);
        int filled = (int)((float) hp / maxHp * barW);
        g.fillRect(bx, by, filled, barH);
        g.setColor(Color.WHITE);
        g.drawRect(bx, by, barW, barH);
        g.drawString("HP: " + hp + "/" + maxHp, bx, by + barH + 14);
    }

    // ── Setters/Getters ────────────────────────────────────────────────────────

    public void setLeft(boolean b)      { left  = b; }
    public void setRight(boolean b)     { right = b; }
    public void setJump(boolean b)      { jump  = b; }
    public void setAttacking(boolean b) { attacking = b; }
    public void setWantsToShoot(boolean b){ wantsToShoot = b; }
    public void setSelectedPellet(int i){ selectedPellet = Math.max(0, Math.min(3, i)); }
    public int  getSelectedPellet()     { return selectedPellet; }
    public boolean isFacingRight()      { return facingRight; }

    public void resetDirBooleans() {
        left = false; right = false; jump = false;
    }

    public boolean isDead()     { return dead; }
    public long    getDeathTime(){ return deathTime; }

    @Override
    public void takeDamage(int dmg) {
        super.takeDamage(dmg);
        if (!alive && !dead) {
            dead      = true;
            deathTime = System.currentTimeMillis();
        }
    }

    public void respawn(float x, float y) {
        hitbox.x  = x;
        hitbox.y  = y;
        hp        = maxHp;
        alive     = true;
        dead      = false;
        deathTime = -1;
        inAir     = true;
        airSpeed  = 0;
        resetDirBooleans();
        resetAnimationTick();
    }

    public void setSkin(String skin) { loadAnimations(skin); }
    public String getCurrentSkin()   { return currentSkin; }
}
