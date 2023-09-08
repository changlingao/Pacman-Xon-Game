package lawnlayer;

import processing.core.PImage;
import java.util.Random;

public class Enemy extends StillObject{
    // protected for subclass ie Beetle
    /** movement speed in x */
    protected int SpeedX;
    /** movement speed in y */
    protected int SpeedY;
    /** frozen timer caused by the powerup */
    protected int timer;
    /** the head in red propagation */
    protected Tile pointer1;
    /** the head in red propagation */
    protected Tile pointer2;

    /**
     * Constructor for an enemy.
     * default speeds in X, Y are 2 pixels per frame.
     * @param map, to find an empty tile to spawn
     * @param spawn, spawn location
     * @param sprite, enemy image
     */
    public Enemy(Tile[][] map, String spawn, PImage sprite) {
        // within empty space
        if (spawn.equals("random")) {
            Random random = new Random();
            int map_i = random.nextInt(map.length);
            int map_j = random.nextInt(map[0].length);
            while(map[map_i][map_j].getTileType() == TileType.CONCRETE) {
                map_i = random.nextInt(map.length);
                map_j = random.nextInt(map[0].length);
            }
            this.x = map_j * 20;
            this.y = map_i*20 + 80;
        } else {
            String[] coordinates = spawn.split(",");
            this.x = Integer.parseInt(coordinates[1])*20;
            this.y = Integer.parseInt(coordinates[0])*20 + 80;
        }
        this.sprite = sprite;
        this.SpeedX = 2;
        this.SpeedY = 2;
        this.timer = -1;
    }
    
    public void tick(Tile[][] map) {

        if (pointer1!=null || pointer2!=null) {
            redPropagate(map);
        }

        // check collision
        for(int i=0; i<map.length; i++){
            for(int j=0; j<map[0].length; j++){
                Tile tile = map[i][j];
                if (tile.getTileType() == TileType.SOIL)
                    continue;
                boolean collision = false;
                // check X bounce
                if (this.x + this.width + this.SpeedX > tile.getX() && 
                    this.x + this.SpeedX < tile.getX() + tile.getWidth() && 
                    this.y + this.height > tile.getY() &&
                    this.y < tile.getY() + tile.getHeight()) {
                        SpeedX *= -1;
                        collision = true;
                    }
                // check Y bounce
                if (this.x + this.width > tile.getX() && 
                    this.x < tile.getX() + tile.getWidth() && 
                    this.y + this.height + this.SpeedY > tile.getY() &&
                    this.y + this.SpeedY < tile.getY() + tile.getHeight()) {
                        SpeedY *= -1;
                        collision = true;
                    }
                
                // check collision type
                if (collision == true && tile.getTileType() == TileType.GRASS) {
                    this.hitGrass(tile);
                }
                if (collision == true && tile.getTileType() == TileType.GREEN_PATH){
                    tile.setTileType(TileType.RED_PATH);
                    Tile neighbor1 = tile.nextGreenNeighbor(map);
                    if (neighbor1 != null)
                        neighbor1.setTileType(TileType.RED_PATH);
                    pointer1 = neighbor1;
                    Tile neighbor2 = tile.nextGreenNeighbor(map);
                    if (neighbor2 != null)
                        neighbor2.setTileType(TileType.RED_PATH);
                    pointer2 = neighbor2;
                }
            }    
        }
        // freeze check
        if (this.timer >= 0) {
            this.timer -= 1;
        } else {
            this.x += this.SpeedX;
            this.y += this.SpeedY;
        }
    }

    /**
     * Start freeze timer
     * @param seconds, freeze for how long in seconds
     * @param FPS, the game's frame
     */
    public void freeze(int seconds, int FPS) {
        this.timer = seconds * FPS;
    }

    /** 
     * Normal Enemy does nothing,
     * Bettle turn the tile to soil 
     * @param tile, that the enemy hits
     */
    public void hitGrass(Tile tile) {
        
    }

    /**
     * If the enemy hits the green path,
     * the tile turns red and propagates.
     * @param map, used to calculate the propagation path
     */
    public void redPropagate(Tile[][] map) {
        if (pointer1!=null) {
            Tile neighbor1 = pointer1.nextGreenNeighbor(map);
            if (neighbor1!= null) {
                neighbor1.setTileType(TileType.RED_PATH);
            }
            pointer1 = neighbor1;
        }
        if (pointer2!=null) {
            Tile neighbor2 = pointer2.nextGreenNeighbor(map);
            if (neighbor2!= null) {
                neighbor2.setTileType(TileType.RED_PATH);
            }
            pointer2 = neighbor2;
        }
    }

}
