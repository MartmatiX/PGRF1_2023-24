package cz.uhk.fim.raster_op;

import cz.uhk.fim.object_data.Point;
import cz.uhk.fim.raster_data.Raster;

/**
 * Represents an algorithm for drawing straight lines into the Raster
 */

public interface Liner {

    /**
     * Draws a straight line with specific color from x1;y1 to x2;y2 on given Raster
     *
     * @param img   - raster to which the line should be drawn into
     * @param x1    - first x coordinate
     * @param y1    - first y coordinate
     * @param x2    - second x coordinate
     * @param y2    - second y coordinate
     * @param color - color of the line
     */
    void drawLine(Raster img, double x1, double y1, double x2, double y2, int color);

    default void drawLine(Raster img, Point p1, Point p2, int color) {
        drawLine(img, p1.getX(), p1.getY(), p2.getX(), p2.getY(), color);
    }

    default void drawPixel(Raster img, Point point, int color) {
        img.setColor((int) point.getX(), (int) point.getY(), color);
    }

}
