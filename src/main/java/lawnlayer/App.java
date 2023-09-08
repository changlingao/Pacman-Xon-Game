package lawnlayer;

import org.checkerframework.checker.units.qual.A;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PFont;
import processing.data.JSONObject;
import processing.data.JSONArray;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;

public class App extends PApplet {

    /** window size */
    public static final int WIDTH = 1280;
    /** window size */
    public static final int HEIGHT = 720;
    /** a frame rate of 60 frames per second */
    public static final int FPS = 60;
    /** the path for config.json file */
    public String configPath;
    /** customized font for the top bar */
    public PFont myFont;
    /** the level for one level */
    public int level = 0;
    /** the goal for one level */
    public double goal;
	
	public PImage grass;
    public PImage concrete;
    public PImage worm;
    public PImage beetle;
    public PImage ball;
    /** soil image */
    public PImage soil;
    /** green path image */
    public PImage green;
    /** red path image */
    public PImage red;
    /** frozen powerup image */
    public PImage blue;
    /** speed up powerup image */
    public PImage orange;

    /** 32 tiles in the height of the window */
    public static final int height = 32;
    /** 64 tiles in the height of the window */
    public static final int width = 64;
    /** 2-dimensional array to store tile map */
    public Tile[][] map = new Tile[height][width];
    public Player player;
    /** a list to store enemies */
    public List<Enemy> enemies;
    public Powerup powerup;
    /** customized effect time */
    public static final int EFFECT_SECONDS = 6;
    /** customized spawn wait time */
    public static final int POWERUP_SPAWN = 5;
    /**
     * Timer for frame. 
     * bigger than 0: need to be shown;
     * -1: no timer but strat of next poweup;
     * -2: no timer no start
     */
    public int effect_timer = -2;
    /**
     * Timer for frame.
     * powerup_timer aka production interval.
     * bigger than 0: in progress;
     * 0: wait to produce;
     * -1: exists one;
     * -2: pause and wait frozen finishes
     */
    public int powerup_timer = 0;

    /**
     * Constructor for an app.
     */
    public App() {
        this.configPath = "config.json";
        this.enemies = new ArrayList<Enemy>();
    }
    /**
     * Initialise the setting of the window size.
     */
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /** It's called once when the program starts.
     * Setup my font.
     * Load all images.
     * Initialise the player and enemies.
     */
    public void setup() {
        frameRate(FPS);
        // available fonts in the computer
        // String[] fontList = PFont.list(); printArray(fontList);
        myFont = createFont("SansSerif", 40);
        textFont(myFont);

        // Load images during setup; the process of loading the image from the hard drive into memory is a slow one, and we should make sure our program only has to do it once, in setup()
		this.grass = loadImage(this.getClass().getResource("grass.png").getPath());
        this.concrete = loadImage(this.getClass().getResource("concrete_tile.png").getPath());
        this.worm = loadImage(this.getClass().getResource("worm.png").getPath());
        this.beetle = loadImage(this.getClass().getResource("beetle.png").getPath());
        this.ball = loadImage(this.getClass().getResource("ball.png").getPath());
        this.colorSetup();

        // player setup
        this.player = new Player();
        // only once
        int lives = loadJSONObject(this.configPath).getInt("lives");
        player.setLives(lives);
        player.setSprite(ball);
        // map enemies
        this.newLevel(level);
    }
	
    /** 
     * Draw all elements in the game by current frame. 
     * Draw the background and the tile map.
     * Draw the player, enemies and powerup.
     * Draw the top bar.
     * Check the next level.
     * Check player's lives.
     */
    public void draw() {
        background(82, 54, 27);
        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                if (map[i][j].getTileType() == TileType.SOIL){
                    map[i][j].setSprite(soil);
                } else if (map[i][j].getTileType() == TileType.GRASS){
                    map[i][j].setSprite(grass);
                } else if (map[i][j].getTileType() == TileType.GREEN_PATH){
                    map[i][j].setSprite(green);
                } else if (map[i][j].getTileType() == TileType.RED_PATH){
                    map[i][j].setSprite(red);
                }
                map[i][j].draw(this);
            }
        }
        player.tick(map, enemies);
        player.draw(this);
        for (Enemy enemy: enemies) {
            enemy.tick(map);
            enemy.draw(this);
        }
        this.PowerUp();

        // next level check
        int grass_num = 0;
        int soil_num = 0;
        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                if (map[i][j].getTileType() == TileType.GRASS){
                    grass_num += 1;
                } else if (map[i][j].getTileType() == TileType.SOIL){
                    soil_num += 1;
                }
            }
        }
        int result = (int) Math.floor(grass_num/1.0/(grass_num+soil_num) *100);
        // topbar print
        text("      "+"Level: "+level+ "       "+"Lives: "+player.getLives() + "        "+
            result+"%/"+Math.round(goal*100)+"%", 0, 50);
        

        if (grass_num/1.0/(grass_num+soil_num) >= goal) {
            // can't draw map or text...
            this.level += 1;
            boolean has_nextlevel = this.newLevel(level);
            powerup_timer = 0; // reset powerup to spawn
            if (!has_nextlevel) {
                background(82, 54, 27);
                textAlign(CENTER);
                text("YOU WIN!", WIDTH/2, HEIGHT/2);
            }
        }

        // player lives check
        if (player.getLives() == 0) {
            background(82, 54, 27);
            textAlign(CENTER);
            text("GAME OVER.", WIDTH/2, HEIGHT/2);
        }  
    }

    /**
     * If app has next level, then load it.
     * Use json library to read config file.
     * Setup map and enemies.
     * Restart the player.
     * @param level_index, which level to load
     * @return whether app has next level or not
     */
    public boolean newLevel(int level_index) {
        // config file
        JSONObject config = loadJSONObject(this.configPath);
        JSONArray levels = config.getJSONArray("levels");
        // level_index starts from 0 (level 1)
        if (level_index+1 > levels.size()) {
            return false;
        }

        // map (one level outlay)
        JSONObject level = levels.getJSONObject(level_index);
        this.goal = level.getDouble("goal");
        try {
            File f = new File(level.getString("outlay"));
            Scanner scan = new Scanner(f);
            for(int i=0; i<height; i++){
                String[] characters = scan.nextLine().split("");
                for(int j=0; j<width; j++){
                    if(characters[j].equals("X")){
                        Tile concrete_tile = new Tile(20*j, 20*i+80);
                        concrete_tile.setTileType(TileType.CONCRETE);
                        concrete_tile.setSprite(concrete);
                        this.map[i][j] = concrete_tile;
                    } else {
                        Tile soil_tile = new Tile(20*j, 20*i+80);
                        soil_tile.setTileType(TileType.SOIL);
                        soil_tile.setSprite(soil);
                        this.map[i][j] = soil_tile;
                    }
                }
            }
            scan.close();  
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // enemies setup
        enemies.removeAll(enemies);
        JSONArray enemies_jsonarray = level.getJSONArray("enemies");
        for (int i=0; i< enemies_jsonarray.size(); i++) {
            JSONObject enemy_json = enemies_jsonarray.getJSONObject(i);
            int type = enemy_json.getInt("type");
            String spawn = enemy_json.getString("spawn");
            if (type == 0){
                enemies.add(new Enemy(map, spawn, worm));
            } else if (type == 1) {
                enemies.add(new Beetle(map, spawn, beetle));
            }
        }
        // player restart
        player.restart();
        return true;
    }

    /**
     * If valid key is pressed,
     * the player calls press function.
     */
    public void keyPressed() {
        // LEFT:37  UP:38  RIGHT:39  DOWN:40
        if (this.keyCode >= 37 && this.keyCode <= 40) {
            this.player.press(this.keyCode, this.map);
        }
    }

    /**
     * If it's time to produce a powerup, produce one.
     * If there is a powerup then draw it,
     * and if it collides with the player then produce powerup effect,
     * and if it collides with grass tile then disappear.
     */
    public void PowerUp() {
        // frozen_timer||  >0: need to be shown; -1: no timer but strat of next poweup; -2: no timer no start
        // powerup_timer(production interval)||  >0: in progress; 0: wait to produce; -1: exists one; -2: pause and wait frozen finishes;
        if (powerup_timer == -1) {
            powerup.draw(this);
        }
        // produce a powerup
        if (powerup_timer >= 0 && powerup_timer < POWERUP_SPAWN*FPS) {
            powerup_timer += 1;
        } else if (powerup_timer == POWERUP_SPAWN*FPS) {
            Random random = new Random();
            int type = random.nextInt(2);
            // enemies frozen
            if (type == 0) {
                this.powerup = new Powerup(map, type, blue);
            // player speed up
            } else if (type == 1) {
                this.powerup = new Powerup(map, type, orange);
            }
            powerup_timer = -1;
            // means there is already one
        }

        // powerup coliision
        if (powerup_timer == -1) {
            // powerup collides with player
            if (player.getX() == powerup.getX() && player.getY() == powerup.getY()) {
                powerup_timer = -2;
                powerup.setSprite(soil);
                if (this.powerup.getType() == 0) {
                    for (Enemy enemy: enemies) {
                        enemy.freeze(EFFECT_SECONDS, FPS);
                        effect_timer = EFFECT_SECONDS*FPS;
                    }
                } else if (this.powerup.getType() == 1) {
                    this.player.speedup(EFFECT_SECONDS, FPS);
                    effect_timer = EFFECT_SECONDS*FPS;
                }
            }
            if (powerup.grass_collision(map) == true) {
                powerup_timer = 0;
                powerup.setSprite(grass);
            }
        }

        // effect timer 
        if(effect_timer >= 0) {   // show what
            if (this.powerup.getType() == 0) {
                text("                                                                      "+
                "Frozen Time: "+str(effect_timer/FPS)+" s", 0, 50);
            } else if (this.powerup.getType() == 1) {
                text("                                                                      "+
                "Speedup Time: "+str(effect_timer/FPS)+" s", 0, 50);
            }
            effect_timer -= 1;
        } else if (effect_timer == -1) {
            // finished one frozen, strats the next powerup
            powerup_timer = 0;
            effect_timer -= 1;
        } else if (effect_timer == -2) {
            // no strat of next powerup
        }

    }

    /**
     * Load all customized images aka color blocks.
     */
    public void colorSetup() {
        // green
        this.green = createImage(16, 16, RGB);
        this.green.loadPixels();
        for (int i = 0; i < this.green.pixels.length; i++) {
            this.green.pixels[i] = color(0, 255, 0); 
        }
        this.green.updatePixels();
        // red
        this.red = createImage(16, 16, RGB);
        this.red.loadPixels();
        for (int i = 0; i < this.red.pixels.length; i++) {
            this.red.pixels[i] = color(255, 0, 0); 
        }
        this.red.updatePixels();
        // soil
        this.soil = createImage(20, 20, RGB);
        this.soil.loadPixels();
        for (int i = 0; i < this.soil.pixels.length; i++) {
            this.soil.pixels[i] = color(82, 54, 27); 
        }
        this.soil.updatePixels();
        // blue
        this.blue = createImage(20, 20, RGB);
        this.blue.loadPixels();
        for (int i = 0; i < this.blue.pixels.length; i++) {
            this.blue.pixels[i] = color(0, 0, 255); 
        }
        this.blue.updatePixels();
        // orange
        this.orange = createImage(20, 20, RGB);
        this.orange.loadPixels();
        for (int i = 0; i < this.orange.pixels.length; i++) {
            this.orange.pixels[i] = color(255,99, 71); 
        }
        this.orange.updatePixels();
    }

    public static void main(String[] args) {
        PApplet.main("lawnlayer.App");
    }

    // just for test...
    /**
     * @return the player
     */
    public Player getPlayer() {
        return this.player;
    }
    /**
     * @return the tile map
     */
    public Tile[][] getMap() {
        return this.map;
    }
    /**
     * @return the powerup
     */
    public Powerup getPowerup() {
        return this.powerup;
    }
    
}
