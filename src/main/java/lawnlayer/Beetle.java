package lawnlayer;

import processing.core.PImage;

public class Beetle extends Enemy{

    /**
     * Constructor for a beetle
     * @param map, to find an empty tile to spawn
     * @param spawn, spawn location
     * @param beetle, beetle image
     */
    public Beetle(Tile[][] map, String spawn, PImage beetle) {
        super(map, spawn, beetle);
    }

    /**
     * If the beetle hits a grass tile,
     * set the tile's type to soil.
     * @param tile, that the beetle hits
     */
    public void hitGrass(Tile tile) {
        tile.setTileType(TileType.SOIL);
    }
}
