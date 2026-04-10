package utils;

import main.Game;
import java.awt.geom.Rectangle2D;

public class HelpMethods {

    public static boolean CanMoveHere(float x, float y, float width, float height, int[][] lvlData) {
        if (!IsSolid(x,         y,          lvlData))
        if (!IsSolid(x + width, y + height, lvlData))
        if (!IsSolid(x + width, y,          lvlData))
        if (!IsSolid(x,         y + height, lvlData))
            return true;
        return false;
    }

    public static boolean IsSolid(float x, float y, int[][] lvlData) {
        if (x < 0 || x >= Game.GAME_WIDTH)  return true;
        if (y < 0 || y >= Game.GAME_HEIGHT) return true;
        int xi = (int)(x / Game.TILES_SIZE);
        int yi = (int)(y / Game.TILES_SIZE);
        if (yi >= lvlData.length || xi >= lvlData[0].length || xi < 0 || yi < 0) return true;
        return lvlData[yi][xi] != 89; // 89 = air/transparent
    }

    /** True if solid ground exists just 1 px below the entity's feet. */
    public static boolean IsEntityOnFloor(Rectangle2D.Float h, int[][] lvlData) {
        return IsSolid(h.x,           h.y + h.height + 1, lvlData)
            || IsSolid(h.x + h.width, h.y + h.height + 1, lvlData);
    }

    /**
     * Snap entity so its bottom sits exactly on the top edge of the tile below.
     * Call when downward movement was blocked (airSpeed > 0).
     */
    public static float GetEntityYPosAboveFloor(Rectangle2D.Float h, float fallSpeed) {
        // The tile the BOTTOM of the hitbox just entered
        int tileRow = (int)((h.y + h.height + fallSpeed) / Game.TILES_SIZE);
        return tileRow * Game.TILES_SIZE - h.height - 1;
    }

    /**
     * Snap entity so its top sits just below the tile ceiling.
     * Call when upward movement was blocked (airSpeed < 0).
     */
    public static float GetEntityYPosUnderCeiling(Rectangle2D.Float h, float airSpeed) {
        // The tile the TOP of the hitbox just entered
        int tileRow = (int)((h.y + airSpeed) / Game.TILES_SIZE);
        return (tileRow + 1) * Game.TILES_SIZE + 1;
    }

    /**
     * Snap entity next to the wall it collided with.
     * Call when horizontal movement was blocked.
     */
    public static float GetEntityXPosNextToWall(Rectangle2D.Float h, float xSpeed) {
        int tileCol = (int)(h.x / Game.TILES_SIZE);
        if (xSpeed > 0) {
            // Moving right — right edge hits wall; snap left edge to left of that tile
            return tileCol * Game.TILES_SIZE + Game.TILES_SIZE - h.width - 1;
        } else {
            // Moving left — left edge hits wall; snap to right edge of tile to the left
            return tileCol * Game.TILES_SIZE + 1;
        }
    }

    /** True if the tile below the leading foot is solid (for enemy patrol edge detection). */
    public static boolean IsFloorTileBelow(Rectangle2D.Float h, int[][] lvlData, float xSpeed) {
        float checkX = (xSpeed > 0) ? h.x + h.width + 1 : h.x - 1;
        return IsSolid(checkX, h.y + h.height + 1, lvlData);
    }
}
