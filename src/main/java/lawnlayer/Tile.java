package lawnlayer;

public class Tile extends StillObject {
    private TileType tileType;
    private boolean checked;
    /** the corresponding i in tile matrix */
    private int matrix_i;
    /** the corresponding j in tile matrix */
    private int matrix_j;

    /**
     * Constructor for a tile.
     * @param x, x position
     * @param y, y position
     */
    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
        checked = false;
        matrix_i = (this.y - 80) / 20;
        matrix_j = this.x / 20;
    }

    /**
     * @return tile type
     */
    public TileType getTileType() {
        return tileType;
    }
    /**
     * Set tile type.
     * @param tileType
     */
    public void setTileType(TileType tileType) {
        this.tileType = tileType;
    }

    /**
     * @return if the tile has been checked
     */
    public boolean getChecked() {
        return checked;
    }
    /**
     * Set checked status.
     * @param checked
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    
    /**
     * @return corresponding i in tile matrix
     */
    public int getMatrixI() {
        return matrix_i;
    }
    /**
     * @return corresponding j in tile matrix
     */
    public int getMatrixJ() {
        return matrix_j;
    }

    /**
     * Check if the tile has a neighbor in the tile map.
     * @param keyCode, direction
     * @param map
     * @return if the tile has a neighbor in the tile map
     */
    public boolean hasNeighbor(int keyCode, Tile[][] map) {
        if (keyCode == 37) {
            return (this.matrix_j != 0);
        } else if (keyCode == 38) {
            return (this.matrix_i != 0);
        } else if (keyCode == 39) {
            return (this.matrix_j != map[0].length-1);
        } else if (keyCode == 40) {
            return (this.matrix_i != map.length-1);
        }
        return false;
    }

    /**
     * @param map
     * @return the tile's one green path neighbor in the tile map.
     */
    public Tile nextGreenNeighbor(Tile[][] map) {
        // left neightbor
        if (map[matrix_i][matrix_j-1].getTileType() == TileType.GREEN_PATH) {
            return map[matrix_i][matrix_j-1];
        } // right neighbor
        else if (map[matrix_i][matrix_j+1].getTileType() == TileType.GREEN_PATH) {
            return map[matrix_i][matrix_j+1];
        } // up neighbor
        else if (map[matrix_i-1][matrix_j].getTileType() == TileType.GREEN_PATH) {
            return map[matrix_i-1][matrix_j];
        } // down neighbor
        else if (map[matrix_i+1][matrix_j].getTileType() == TileType.GREEN_PATH) {
            return map[matrix_i+1][matrix_j];
        } 
        return null;
    }

}
