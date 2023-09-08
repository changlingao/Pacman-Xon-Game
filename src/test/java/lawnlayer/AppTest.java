package lawnlayer;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import processing.core.PApplet;
import processing.core.PImage;

public class AppTest {

    @Test
    public void testApp() {
        App app = new App();
        // Tell PApplet to create the worker threads for the program
        PApplet.runSketch(new String[] {"App"}, app);
        app.setup();
        // To give time to initialise stuff before drawing begins
        app.delay(1000); 
        app.draw();

        // test PowerUp
        // powerup collides with player
        Player player = app.getPlayer();
        Tile[][] map1 = app.getMap();
        for(int i=0; i<500; i++)
            app.draw();
        Powerup powerup1 = app.getPowerup();
        player.setXY(powerup1.getX(), powerup1.getY());
        app.draw();
        for(int i=0; i<1000; i++)
            app.draw();
        // another powerup type
        Powerup powerup2 = app.getPowerup();
        if (powerup1.getType() == 0) {
            powerup2.setType(1);
        } else {
            powerup2.setType(0);
        }
        player.setXY(powerup2.getX(), powerup2.getY());
        app.draw();
        for(int i=0; i<1000; i++)
            app.draw();
        // powerup collides with grass
        Powerup powerup3 = app.getPowerup();
        map1[(powerup3.getY()-80)/20][powerup3.getX()/20].setTileType(TileType.GRASS);
        app.draw();

        app.keyPressed();

        // next level
        for(int i=0; i<32; i++){
            for(int j=0; j<63; j++){
                map1[i][j].setTileType(TileType.GRASS);
            }
        }
        app.draw();
        // YOU WIN
        for(int i=1; i<32; i++){
            for(int j=1; j<63; j++){
                map1[i][j].setTileType(TileType.GRASS);
            }
        }
        app.draw();
        // player dead
        player.setLives(0);
        app.draw();
    }

    /**
     * Test Player
     */
    @Test
    public void testPlayer() {
        Tile[][] map = new Tile[32][64];
        for (int i=0; i<32; i++) {
            for (int j=0; j<64; j++) {
                Tile tile = new Tile(20*j, 20*i+80);
                tile.setTileType(TileType.SOIL);
                map[i][j] = tile;
            }
        }
        Player player = new Player();
        player.setLives(3);
        assertEquals(3, player.getLives());
        player.respawn(map);
        // speed up 
        player.setSpeed(2, 0);
        player.speedup(3, 60);
        player.setSpeed(-2, 0);
        player.speedup(3, 60);
        player.setSpeed(0, 2);
        player.speedup(3, 60);
        player.setSpeed(0, -2);
        player.speedup(3, 60);

        // press in soil or grass
        // 37 or 39
        map[6][10].setTileType(TileType.GRASS);
        player.setXY(200, 200);
        player.setSpeed(0, 2);
        player.press(37, map);
        player.setXY(200, 200);
        player.setSpeed(0, 2);
        player.press(39, map);
        // speedX != 0
        player.setXY(200, 200);
        player.setSpeed(5, 2);
        player.press(39, map);
        // 38 or 40
        player.setXY(200, 200);
        player.setSpeed(2, 0);
        player.press(38, map);
        player.setXY(200, 200);
        player.setSpeed(2, 0);
        player.press(40, map);
        // speedY != 0
        player.setXY(200, 200);
        player.setSpeed(2, 3);
        player.press(40, map);
        // keyCode invalid
        player.setXY(200, 200);
        player.setSpeed(2, 0);
        player.press(41, map);

        // press in concrete
        map[6][10].setTileType(TileType.CONCRETE);
        player.setSpeed(0, 2);
        player.setXY(200, 200);
        player.press(37, map);
        player.setXY(200, 200);
        player.press(39, map);
        player.setSpeed(2, 0);
        player.setXY(200, 200);
        player.press(38, map);
        player.setXY(200, 200);
        player.press(40, map);
        player.setXY(200, 200);
        player.press(41, map);

        player.setLives(0);
    }
    
    /**
     * Test StilllObject.
     */
    @Test
    public void testStilllObject() {
        Tile[][] map = new Tile[32][64];
        for (int i=0; i<32; i++) {
            for (int j=0; j<64; j++) {
                Tile tile = new Tile(20*j, 20*i+80);
                tile.setTileType(TileType.SOIL);
                map[i][j] = tile;
            }
        }
        Tile tile = new Tile(100, 100);
        // extends from StilllObject
        assertEquals(100, tile.getX());
        assertEquals(100, tile.getY());
        assertEquals(20, tile.getWidth());
        assertEquals(20, tile.getHeight());
    }

    /** 
     * Test Enemy
     */
    @Test
    public void testEnemy() {
        PImage sprite = new PImage();
        Tile[][] map = new Tile[32][64];
        for (int i=0; i<32; i++) {
            for (int j=0; j<64; j++) {
                Tile tile = new Tile(20*j, 20*i+80);
                tile.setTileType(TileType.SOIL);
                map[i][j] = tile;
            }
        }
        Enemy enemy = new Enemy(map, "random", sprite);
        map[(enemy.getY()-80)/20][enemy.getX()/20+1].setTileType(TileType.GRASS);
        map[(enemy.getY()-80)/20-1][enemy.getX()/20].setTileType(TileType.GRASS);
        // void just call 
        enemy.tick(map);
        enemy.freeze(3, 60);
        enemy.tick(map);

        Enemy enemy2 = new Enemy(map, "10,10", sprite);
        int i = (enemy2.getY()-80)/20;
        int j= enemy2.getX()/20;
        map[i+1][j-2].setTileType(TileType.GREEN_PATH);
        map[i+1][j-1].setTileType(TileType.GREEN_PATH);
        map[i+1][j].setTileType(TileType.GREEN_PATH);
        map[i+1][j+1].setTileType(TileType.GREEN_PATH);
        map[i+1][j+2].setTileType(TileType.GREEN_PATH);
        map[i][j+2].setTileType(TileType.GREEN_PATH);
        map[i+2][j+2].setTileType(TileType.GREEN_PATH);
        enemy2.tick(map);
        enemy2.tick(map);
        enemy2.tick(map);
        enemy2.tick(map);

        for (i=0; i<30; i++) {
            for (j=0; j<60; j++) {
                Tile tile = new Tile(20*j, 20*i+80);
                tile.setTileType(TileType.SOIL);
                map[i][j] = tile;
            }
        }
        Enemy enemy3 = new Enemy(map, "random", sprite);
    }

    /** 
     * Test Beetle
     */
    @Test
    public void testBeetle() {
        PImage sprite = new PImage();
        Tile[][] map = new Tile[32][64];
        for (int i=0; i<32; i++) {
            for (int j=0; j<64; j++) {
                Tile tile = new Tile(20*j, 20*i+80);
                tile.setTileType(TileType.SOIL);
                map[i][j] = tile;
            }
        }
        Enemy beetle = new Beetle(map, "3,5", sprite);
        Tile grass_tile = new Tile(80, 120);
        grass_tile.setTileType(TileType.GRASS);
        beetle.hitGrass(grass_tile);
        assertEquals(TileType.SOIL, grass_tile.getTileType());
    }

    /** 
     * Test Tile
     */
    @Test
    public void testTile() {
        Tile[][] map = new Tile[32][64];
        for (int i=0; i<32; i++) {
            for (int j=0; j<64; j++) {
                Tile tile = new Tile(20*j, 20*i+80);
                tile.setTileType(TileType.SOIL);
                map[i][j] = tile;
            }
        }
        Tile tile = new Tile(100, 100);
        tile.setTileType(TileType.SOIL);
        assertEquals(TileType.SOIL, tile.getTileType());
        tile.setChecked(true);
        assertEquals(true, tile.getChecked());
        assertEquals(1, tile.getMatrixI());
        assertEquals(5, tile.getMatrixJ());
        // test hasNeighbor
        assertEquals(true, tile.hasNeighbor(37, map));
        assertEquals(true, tile.hasNeighbor(38, map));
        assertEquals(true, tile.hasNeighbor(39, map));
        assertEquals(true, tile.hasNeighbor(40, map));
        assertEquals(false, tile.hasNeighbor(41, map));
        tile.hasNeighbor(37, map);
    }

    /** 
     * Test Powerup
     */
    @Test
    public void testPowerup() {
        PImage sprite = new PImage();
        Tile[][] map = new Tile[32][64];
        for (int i=0; i<32; i++) {
            for (int j=0; j<64; j++) {
                Tile tile = new Tile(20*j, 20*i+80);
                tile.setTileType(TileType.SOIL);
                map[i][j] = tile;
            }
        }
        for (int i=0; i<30; i++) {
            for (int j=0; j<60; j++) {
                Tile tile = new Tile(20*j, 20*i+80);
                tile.setTileType(TileType.CONCRETE);
                map[i][j] = tile;
            }
        }
        Powerup powerup = new Powerup(map, 1, sprite);
        assertEquals(1, powerup.getType());
        assertEquals(false, powerup.grass_collision(map));
    }

}   
