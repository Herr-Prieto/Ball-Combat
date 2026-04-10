package levels;

import java.util.List;

public class Level {

    private int[][] lvlData;
    private int[] playerSpawn;       // tile coordinates [x,y]
    private List<int[]> enemySpawns; // list of tile coordinates

    public Level(int[][] lvlData, int[] playerSpawn, List<int[]> enemySpawns) {
        this.lvlData      = lvlData;
        this.playerSpawn  = playerSpawn;
        this.enemySpawns  = enemySpawns;
    }

    public int getSpriteIndex(int x, int y) { return lvlData[y][x]; }
    public int[][] getLevelData()            { return lvlData; }
    public int[] getPlayerSpawn()            { return playerSpawn; }
    public List<int[]> getEnemySpawns()      { return enemySpawns; }
}
