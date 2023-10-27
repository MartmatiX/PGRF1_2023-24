package cz.uhk.fim.raster_op;

import cz.uhk.fim.object_data.Point;
import cz.uhk.fim.raster_data.Raster;
import cz.uhk.fim.raster_data.Rectangle;

/**
 * Class representing a drawer used to draw rectangle onto image
 */
public class RectangleDrawer {

    public void drawRectangle(Raster img, Liner liner, Rectangle rectangle, int color) {
        Point p1 = rectangle.getPoints().get(0);
        Point p2 = rectangle.getPoints().get(1);

        // Horizontal lines
        liner.drawLine(img, p1.getX(), p1.getY(), p2.getX(), p1.getY(), color);
        liner.drawLine(img, p1.getX(), p2.getY(), p2.getX(), p2.getY(), color);

        // Vertical lines
        liner.drawLine(img, p1.getX(), p1.getY(), p1.getX(), p2.getY(), color);
        liner.drawLine(img, p2.getX(), p1.getY(), p2.getX(), p2.getY(), color);
    }

}
