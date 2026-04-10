package objects;

import java.awt.geom.Rectangle2D;
import utils.Constants.ProjectileConstants;

public class Projectile {

    private float x, y;
    private float speedX;
    private boolean fromPlayer;
    private int pelletType;   // 0-3 for player, 0 for enemy
    private boolean active = true;

    private int animTick, animIndex;
    private static final int ANIM_SPEED = 8;

    private Rectangle2D.Float hitbox;

    public Projectile(float x, float y, float speedX, boolean fromPlayer, int pelletType) {
        this.x          = x;
        this.y          = y;
        this.speedX     = speedX;
        this.fromPlayer = fromPlayer;
        this.pelletType = pelletType;

        int size = ProjectileConstants.PLAYER_PELLET_SIZE;
        hitbox = new Rectangle2D.Float(x, y, size * 1.5f, size * 1.5f);
    }

    public void update() {
        if (!active) return;
        x += speedX;
        hitbox.x = x;

        animTick++;
        if (animTick >= ANIM_SPEED) {
            animTick = 0;
            animIndex++;
            int maxFrames = fromPlayer
                ? ProjectileConstants.PLAYER_PELLET_FRAMES
                : ProjectileConstants.ENEMY_PELLET_FRAMES;
            if (animIndex >= maxFrames) animIndex = 0;
        }
    }

    public float getX()         { return x; }
    public float getY()         { return y; }
    public boolean isFromPlayer(){ return fromPlayer; }
    public boolean isActive()   { return active; }
    public void setActive(boolean b){ active = b; }
    public int getPelletType()  { return pelletType; }
    public int getAnimIndex()   { return animIndex; }
    public Rectangle2D.Float getHitbox(){ return hitbox; }
}
