package utils;

public class Constants {

    public static class Directions {
        public static final int LEFT  = 0;
        public static final int UP    = 1;
        public static final int RIGHT = 2;
        public static final int DOWN  = 3;
    }

    public static class PlayerConstants {
        public static final int IDLE      = 0;
        public static final int MOVING    = 1;
        public static final int ATTACKING = 2;
        public static final int JUMPING   = 3;
        public static final int HIT       = 4;

        public static int GetSpriteRow(int action, boolean facingRight) {
            switch (action) {
                case IDLE:     return facingRight ? 0 : 2;
                case MOVING:
                case JUMPING:  return facingRight ? 1 : 3;
                case ATTACKING:return facingRight ? 4 : 5;
                case HIT:      return facingRight ? 6 : 7;
                default:       return 0;
            }
        }

        public static int GetSpriteAmount(int action) {
            switch (action) {
                case IDLE:
                case HIT:      return 3;
                case MOVING:
                case JUMPING:  return 4;
                case ATTACKING:return 5;
                default:       return 1;
            }
        }

        public static final int SPRITE_COLS = 5;
        public static final int SPRITE_ROWS = 8;
        public static final int SPRITE_SIZE = 32;
        public static final int MAX_HP = 5;
    }

    public static class EnemyConstants {
        // Enemy_01.png: 12 cols x 6 rows, 32x32px each (384x192)
        public static final int IDLE_LEFT    = 0;
        public static final int IDLE_RIGHT   = 1;
        public static final int ATTACK_LEFT  = 2;
        public static final int ATTACK_RIGHT = 3;
        public static final int DEAD_LEFT    = 4;
        public static final int DEAD_RIGHT   = 5;

        public static final int SPRITE_COLS = 12;
        public static final int SPRITE_ROWS = 6;
        public static final int SPRITE_SIZE = 32;

        public static final int MAX_HP = 10;
        public static final int DETECTION_RANGE_TILES = 5;

        public static int GetSpriteAmount(int state) {
            return 6;
        }
    }

    public static class ProjectileConstants {
        // Player_pellets.png: 3 cols x 4 rows, 16x16px (48x64)
        public static final int PLAYER_PELLET_TYPES  = 4;
        public static final int PLAYER_PELLET_FRAMES = 3;
        public static final int PLAYER_PELLET_SIZE   = 16;

        // Enemy_pellets.png: 4 cols x 1 row, 16x16px (64x16)
        public static final int ENEMY_PELLET_FRAMES  = 4;
        public static final int ENEMY_PELLET_SIZE    = 16;

        public static final int PROJECTILE_DAMAGE    = 1;
        public static final float PLAYER_PELLET_SPEED = 3.0f;
        public static final float ENEMY_PELLET_SPEED  = 1.5f;
    }

    public static final int[] KONAMI_CODE = {
        java.awt.event.KeyEvent.VK_UP,
        java.awt.event.KeyEvent.VK_UP,
        java.awt.event.KeyEvent.VK_DOWN,
        java.awt.event.KeyEvent.VK_DOWN,
        java.awt.event.KeyEvent.VK_LEFT,
        java.awt.event.KeyEvent.VK_RIGHT,
        java.awt.event.KeyEvent.VK_LEFT,
        java.awt.event.KeyEvent.VK_RIGHT,
        java.awt.event.KeyEvent.VK_B,
        java.awt.event.KeyEvent.VK_A
    };
}
