package lawnlayer;

import processing.core.PImage;
import processing.core.PApplet;

public abstract class StillObject {
    /** x position */
    protected int x;
    /** y position */
    protected int y;
    /** object image */
    protected PImage sprite;
    /** object width */
    protected int width;
    /** object height */
    protected int height;

    /** 
     * Constructor for a still object.
     * Default width and height are 20.
     */
    public StillObject() {
        this.width = 20;
        this.height = 20;
    };

    /**
     * @return x position
     */
    public int getX() {
        return this.x;
    }
    /**
     * @return y position
     */
    public int getY() {
        return this.y;
    }
    /**
     * @return width
     */
    public int getWidth() {
        return this.width;
    }
    /**
     * @return height
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Set sprite.
     * @param sprite
     */
    public void setSprite(PImage sprite) {
        this.sprite = sprite;
    }

    /**
     * Draw the object in app.
     * @param app
     */
    public void draw(PApplet app) {
        app.image(this.sprite, this.x, this.y);
    }

}
