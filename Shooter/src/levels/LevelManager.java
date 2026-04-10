package levels;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;

import main.Game;
import utils.LoadSave;

public class LevelManager {

    private Game game;
    private BufferedImage[] levelSprite;

    private Level levelOne;
    private Level levelTwo;
    private Level currentLevel;
    private int currentLevelIndex = 0;

    public LevelManager(Game game) {
        this.game = game;
        importOutsideSprites();
        loadLevels();
    }

    private void importOutsideSprites() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);
        if (img == null) { levelSprite = new BufferedImage[90]; return; }

        levelSprite = new BufferedImage[90];
        // mapmodel_01.png: 576x160 = 18 cols x 5 rows, 32x32 each
        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < 18; i++) {
                int index = j * 18 + i;
                if (index < 90)
                    levelSprite[index] = img.getSubimage(i * 32, j * 32, 32, 32);
            }
        }
    }

    private void loadLevels() {
        levelOne = new Level(
            LoadSave.GetLevelData(LoadSave.LEVEL_ONE_DATA),
            LoadSave.GetPlayerSpawn(LoadSave.LEVEL_ONE_DATA),
            LoadSave.GetEnemySpawns(LoadSave.LEVEL_ONE_DATA)
        );
        levelTwo = new Level(
            LoadSave.GetLevelData(LoadSave.LEVEL_TWO_DATA),
            LoadSave.GetPlayerSpawn(LoadSave.LEVEL_TWO_DATA),
            LoadSave.GetEnemySpawns(LoadSave.LEVEL_TWO_DATA)
        );
        currentLevel = levelOne;
    }

    public void setLevel(int index) {
        currentLevelIndex = index;
        currentLevel = (index == 0) ? levelOne : levelTwo;
    }

    public int getCurrentLevelIndex() { return currentLevelIndex; }

    public void draw(Graphics g) {
        for (int j = 0; j < Game.TILES_IN_HEIGHT; j++) {
            for (int i = 0; i < Game.TILES_IN_WIDTH; i++) {
                int index = currentLevel.getSpriteIndex(i, j);
                if (index < 89 && levelSprite[index] != null) // 89 = air, skip
                    g.drawImage(levelSprite[index],
                        Game.TILES_SIZE * i, Game.TILES_SIZE * j,
                        Game.TILES_SIZE, Game.TILES_SIZE, null);
            }
        }
    }

    public void update() {}

    public Level getCurrentLevel() { return currentLevel; }
}
