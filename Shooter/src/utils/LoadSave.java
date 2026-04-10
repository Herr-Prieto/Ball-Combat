package utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import main.Game;

public class LoadSave {

    // Resource file names
    public static final String PLAYER_ATLAS      = "Player_01.png";
    public static final String PLAYER_ATLAS_2    = "Player_02.png";
    public static final String PLAYER_ATLAS_3    = "Player_03.png";
    public static final String LEVEL_ATLAS       = "mapmodel_01.png";
    public static final String LEVEL_ONE_DATA    = "level_one_data.png";
    public static final String LEVEL_TWO_DATA    = "level_two_data.png";
    public static final String ENEMY_ATLAS       = "Enemy_01.png";
    public static final String PLAYER_PELLETS    = "Player_pellets.png";
    public static final String ENEMY_PELLETS     = "Enemy_pellets.png";
    public static final String BACKGROUND        = "Background.png";
    public static final String MENU_BACKGROUND   = "menu_background.png";
    public static final String MENU_BUTTONS      = "main_menu_buttons.png";
    public static final String PAUSE_MENU        = "pause_menu.png";
    public static final String PAUSE_BUTTONS     = "pause_menu_buttons.png";
    public static final String VOLUME_SLIDER     = "volume_slider.png";
    public static final String SOUND_BUTTON      = "sound_button.png";
    public static final String OMNI_GEIDA        = "OmniGeida.png";
    public static final String MUSIC_FILE        = "Acid_Network.mp3";
    public static final String SHOOT_SOUND       = "Shooting.wav";
    public static final String MENU_PANEL		 = "menu_panel.png";
    
    public static BufferedImage GetSpriteAtlas(String fileName) {
        BufferedImage img = null;
        InputStream is = LoadSave.class.getResourceAsStream("/" + fileName);
        if (is == null) {
            System.err.println("Resource not found: " + fileName);
            return null;
        }
        try {
            img = ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try { is.close(); } catch (IOException e) { e.printStackTrace(); }
        }
        return img;
    }

    /**
     * Loads level tile data from bitmap (red channel 0-89 = tile index).
     * Also extracts spawn points from blue channel:
     *   blue == 1 → player spawn
     *   blue == 2 → enemy spawn
     */
    public static int[][] GetLevelData(String levelFile) {
        int[][] lvlData = new int[Game.TILES_IN_HEIGHT][Game.TILES_IN_WIDTH];
        BufferedImage img = GetSpriteAtlas(levelFile);
        if (img == null) return lvlData;

        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int value = color.getRed();
                // Pixels marked as spawn points (blue channel > 0) are treated as air
                if (color.getBlue() > 0) value = 89;
                else if (value >= 90)    value = 0;
                lvlData[j][i] = value;
            }
        }
        return lvlData;
    }

    /** Returns player spawn tile [x, y] from level bitmap (blue == 1). Default: (1,1). */
    public static int[] GetPlayerSpawn(String levelFile) {
        BufferedImage img = GetSpriteAtlas(levelFile);
        if (img == null) return new int[]{1, 1};
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color c = new Color(img.getRGB(i, j));
                if (c.getBlue() == 1)
                    return new int[]{i, j};
            }
        }
        return new int[]{1, 1};
    }

    /** Returns list of enemy spawn tile positions [x, y] from level bitmap (blue == 2). */
    public static List<int[]> GetEnemySpawns(String levelFile) {
        List<int[]> spawns = new ArrayList<>();
        BufferedImage img = GetSpriteAtlas(levelFile);
        if (img == null) return spawns;
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color c = new Color(img.getRGB(i, j));
                if (c.getBlue() == 2)
                    spawns.add(new int[]{i, j});
            }
        }
        return spawns;
    }
}
