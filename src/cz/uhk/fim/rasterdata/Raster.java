package cz.uhk.fim.rasterdata;

import java.awt.*;
import java.util.Optional;

public interface Raster {

    /**
     * @return width of the canvas
     */
    int getWidth();

    /**
     * @return height of the canvas
     */
    int getHeight();

    /**
     *  Sets the color of the pixel
     * @param x - x coordinate in the canvas
     * @param y - y coordinate in the canvas
     * @param color - color to which the pixel should be set to
     */
    void setColor(int x, int y, int color);

    /**
     *
     * @param x - x coordinate in the canvas
     * @param y - y coordinate in the canvas
     * @return Optional of Integer from which can be extracted the color of the pixel, or null
     */
    Optional<Integer> getColor(int x, int y);

    /**
     * Clears the canvas to the color of background
     * @param background - color to which all pixels should be set to after clearing
     */
    void clear(int background);

    /**
     *
     * @return graphics of the image
     */
    Graphics getGraphics();

}
