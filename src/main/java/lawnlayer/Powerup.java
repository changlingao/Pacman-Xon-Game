package lawnlayer;

import processing.core.PImage;
import java.util.Random;

public class Powerup extends StillObject{
    /** 
     * The powerup type.
     * 0: enemies freeze;
     * 1: player speeds up
     */
    private int type;

    /**
     * @return powerup type
     */
    public int getType() {
        return this.type;
    }
    /**
     * set powerup type
     * @param type
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Constructor for a powerup.
     * @param map, to find an empty tile to spawn
     * @param type, powerup type
     * @param sprite, powerup image
     */
    public Powerup(Tile[][] map, int type, PImage sprite) {
        // within empty space
        Random random = new Random();
        int map_i = random.nextInt(map.length);
        int map_j = random.nextInt(map[0].length);
        while(map[map_i][map_j].getTileType() != TileType.SOIL) {
            map_i = random.nextInt(map.length);
            map_j = random.nextInt(map[0].length);
        }
        this.x = map_j * 20;
        this.y = map_i*20 + 80;
        this.type = type;
        this.sprite = sprite;
    }

    /**
     * @param map
     * @return if the powerup collides with grass tiles.
     */
    public boolean grass_collision(Tile[][] map) {
        Tile tile = map[(this.y - 80) / 20][this.x / 20];
        if (tile.getTileType() == TileType.GRASS) {
            return true;
        }
        return false;
    }

}
