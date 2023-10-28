package cz.uhk.fim.raster_op;

import cz.uhk.fim.object_data.Point;
import cz.uhk.fim.raster_data.Polygon;
import cz.uhk.fim.raster_data.Raster;

/**
 * Class representing a drawer used to draw rectangle onto image
 */
public class RectangleDrawer implements Drawer {

    /**
     * This method takes first two Points added to the array and creates a rectangle that is perpendicular to the axes
     *
     * @param img       - buffered image to which the polygon should be drawn onto
     * @param liner     - Liner class that should be used to draw the polygon
     * @param rectangle - polygon that will be drawn onto the image
     * @param color     - color of the polygon
     */
    @Override
    public void drawPolygon(Raster img, Liner liner, Polygon rectangle, int color) {
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
