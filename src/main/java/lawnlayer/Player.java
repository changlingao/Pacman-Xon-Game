package lawnlayer;

import java.util.List;
import java.util.ArrayList;

public class Player extends StillObject{
    /** movement speed in x */
    private int SpeedX;
    /** movement speed in y */
    private int SpeedY;
    /** Speedup timer caused by the powerup */
    private int timer;
    /** movement speed */
    private int speed;
    private int lives;

    /**
     * Constructor for an player.
     * Default position is top-left corner.
     * Movement speed is 2 pixels per frame.
     */
    public Player() {
        // begins in the top-left corner
        this.x = 0;
        this.y = 80;
        this.SpeedX = 0;
        this.SpeedY = 0;
        this.speed = 2;
    }

    /**
     * Set the x, y position.
     * @param x, x position
     * @param y, y position
     */
    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }
    /**
     * Set the x, y speed.
     * @param SpeedX, x speed
     * @param SpeedY, y speed
     */
    public void setSpeed(int SpeedX, int SpeedY) {
        this.SpeedX = SpeedX;
        this.SpeedY = SpeedY;
    }

    /**
     * @return the lives of the player
     */
    public int getLives() {
        return this.lives;
    }
    /**
     * Set the lives of the player.
     * @param lives, the lives of the player
     */
    public void setLives(int lives) {
        this.lives = lives;
    }

    /**
     * The movement per frame.
     * Check enemies collision.
     * Check tile exact collision.
     * Check red path hit.
     * The x, y positions increment by speed in x, y.
     * @param map, to check tile collision
     * @param enemies, to check enemies collision
     */
    public void tick(Tile[][] map, List<Enemy> enemies) {
        // check enemies collision
        for (Enemy enemy : enemies) {
            if (this.x == enemy.getX() && this.y == enemy.getY()) {
                this.respawn(map);
            }
        }
        // check tile exact collision
        for(int i=0; i<map.length; i++){
            for(int j=0; j<map[0].length; j++){
                Tile tile = map[i][j];
                boolean collision = false;
                if (this.x == tile.getX() && this.y == tile.getY()) {
                    collision = true;
                }
                if (collision == false)
                    continue;
                
                if (tile.getTileType() == TileType.CONCRETE) {
                    SpeedX = 0;
                    SpeedY = 0;
                    this.x = tile.getX();
                    this.y = tile.getY();
                    fillSoil(map, enemies);
                } else if (tile.getTileType() == TileType.GRASS) {
                    fillSoil(map, enemies);
                } else if (tile.getTileType() == TileType.SOIL) {
                    tile.setTileType(TileType.GREEN_PATH);
                } else if (tile.getTileType() == TileType.GREEN_PATH) {
                    this.respawn(map);
                }
            }
        }
        // check red path hit
        Tile exact_behind = map[(this.y-80)/20][this.x/20];
        if (exact_behind.getTileType() == TileType.RED_PATH) {
            this.respawn(map);
        }

        this.x += this.SpeedX;
        this.y += this.SpeedY;
        if (this.timer >= 0) {
            this.timer -= 1;
        } else {
            this.speed = 2;
        }
    }

    /**
     * Speed up caused by the powerup.
     * Increase the speed to 4 pixels per frame.
     * @param seconds, for how long in seconds
     * @param FPS, the game's frame
     */
    public void speedup(int seconds, int FPS) {
        this.speed = 4;
        if (SpeedX < 0) {
            SpeedX = -speed;
        } else if (SpeedX > 0) {
            SpeedX = speed;
        }
        if (SpeedY < 0) {
            SpeedY = -speed;
        } else if (SpeedY > 0) {
            SpeedY = speed;
        }
        this.timer = seconds*FPS;
    }

    /**
     * The player respawns.
     * Clean the green path or red path.
     * @param map
     */
    public void respawn(Tile[][] map) {   
        this.lives -= 1;
        this.x = 0;
        this.y = 80;
        this.SpeedX = 0;
        this.SpeedY = 0;
        
        for(int i=0; i<map.length; i++){
            for(int j=0; j<map[0].length; j++){
                Tile tile = map[i][j];
                if (tile.getTileType() == TileType.GREEN_PATH || tile.getTileType() == TileType.RED_PATH) {
                    tile.setTileType(TileType.SOIL);
                }
            }
        } 
    }

    /**
     * The player restarts.
     */
    public void restart() {
        this.x = 0;
        this.y = 80;
        this.SpeedX = 0;
        this.SpeedY = 0;
    }

    /**
     * Change speed and displacement.
     * Different changes in different tiles.
     * @param keyCode, change direction
     * @param map
     */
    // LEFT:37  UP:38  RIGHT:39  DOWN:40
    public void press(int keyCode, Tile[][] map) {
        Tile tile = map[(this.y - 80)/20][this.x/20];
        // in concrete tiles
        if (tile.getTileType() == TileType.CONCRETE) {
            if (keyCode == 37) {
                if (tile.hasNeighbor(keyCode, map)) {
                    this.x -= speed;
                    this.SpeedX = -speed;
                }
            } else if (keyCode == 38) {
                if (tile.hasNeighbor(keyCode, map)) {
                    this.y -= speed;
                    this.SpeedY = -speed;
                }
            } else if (keyCode == 39) {
                if (tile.hasNeighbor(keyCode, map)) {
                    this.x += speed;
                    this.SpeedX = speed;
                }
            } else if (keyCode == 40) {
                if (tile.hasNeighbor(keyCode, map)) {
                    this.y += speed;
                    this.SpeedY = speed;
                }
            }
        }
        // in grass and soil tiles
        else {
            if (keyCode == 37 || keyCode == 39) {
                if (SpeedX == 0) {
                    if (SpeedY == -speed) {
                        this.y = this.y/20 *20;
                    } else if (SpeedY == speed) {
                        this.y = (this.y/20+1) *20;
                    }
                    SpeedY = 0;
                    if (keyCode == 37) {
                        SpeedX = -speed;
                    } else if (keyCode == 39) {
                        SpeedX = speed;
                    }
                }
            } else if (keyCode == 38 || keyCode == 40) {
                if (SpeedY == 0) {
                    if (SpeedX == -speed) {
                        this.x = this.x/20 *20;
                    } else if (SpeedX == speed) {
                        this.x = (this.x/20+1) *20;
                    }
                    SpeedX = 0;
                    if (keyCode == 38) {
                        SpeedY = -speed;
                    } else if (keyCode == 40) {
                        SpeedY = speed;
                    }
                }
            }
        }

    }

    /**
     * When the path is closed,
     * fill the non-enemies regions by grass.
     * @param map
     * @param enemies
     */
    public static void fillSoil(Tile[][] map, List<Enemy> enemies){
        int height = map.length;
        int width = map[0].length;
        // fill green or red path 
        for (int i=0; i<height; i++){
            for (int j=0; j<width; j++){
                if (map[i][j].getTileType() == TileType.GREEN_PATH || map[i][j].getTileType() == TileType.RED_PATH)
                    map[i][j].setTileType(TileType.GRASS);;
            }
        }
        
        while (true) {
            // find start
            Tile start = map[0][0];  // just store
            label: for (int i=0; i<height; i++){
                for (int j=0; j<width; j++){
                    if (map[i][j].getTileType() == TileType.SOIL && map[i][j].getChecked() == false) {
                        start = map[i][j];
                        start.setChecked(true);
                        break label;
                    }
                }
            }
            // all tiles are checked
            if (start.getMatrixI()==0 && start.getMatrixJ()==0) {
                break;
            }
            // height width for matrix
            List<Tile> current_side = new ArrayList<Tile>();
            current_side.add(start);
            // Check adjacent tiles, if it's soil add it to current_side
            for(int i=0; i<current_side.size(); i++) {
                Tile temp_tile = current_side.get(i);
                int temp_i = temp_tile.getMatrixI();
                int temp_j = temp_tile.getMatrixJ();
                // left neighbor
                if (map[temp_i][temp_j-1].getTileType() == TileType.SOIL
                    && !current_side.contains(map[temp_i][temp_j-1])) {
                    map[temp_i][temp_j-1].setChecked(true);
                    current_side.add(map[temp_i][temp_j-1]);
                }
                // right neighbor
                if (map[temp_i][temp_j+1].getTileType() == TileType.SOIL
                    && !current_side.contains(map[temp_i][temp_j+1])) {
                    map[temp_i][temp_j+1].setChecked(true);
                    current_side.add(map[temp_i][temp_j+1]);
                }
                // up neighbor
                if (map[temp_i-1][temp_j].getTileType() == TileType.SOIL
                    && !current_side.contains(map[temp_i-1][temp_j])) {
                    map[temp_i-1][temp_j].setChecked(true);
                    current_side.add(map[temp_i-1][temp_j]);
                }
                // down neighbor
                if (map[temp_i+1][temp_j].getTileType() == TileType.SOIL
                    && !current_side.contains(map[temp_i+1][temp_j])) {
                    map[temp_i+1][temp_j].setChecked(true);
                    current_side.add(map[temp_i+1][temp_j]);
                }
            }
            // if enemies are not in the region, fill it in with grass
            boolean has_enemy = false;
            for (Enemy enemy: enemies) {
                for (Tile tile: current_side){
                    if(enemy.getX()+enemy.getWidth() > tile.getX() && 
                        enemy.getX() < tile.getX()+tile.getWidth() && 
                        enemy.getY()+enemy.getHeight() > tile.getY() &&
                        enemy.getY() < tile.getY()+tile.getHeight()){
                            has_enemy = true;
                    }
                }
            }
            if (has_enemy == false) {
                for (Tile tile: current_side){
                    tile.setTileType(TileType.GRASS);   
                }
            }
        }
        // Reset Tile.checked after one fillsoil is done; Tile.checked is especially for finding the start tile
        for (int i=0; i<height; i++){
            for (int j=0; j<width; j++){
                if (map[i][j].getTileType() == TileType.SOIL) {
                    map[i][j].setChecked(false);
                }
            }
        }
    }
}
