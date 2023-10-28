package cz.uhk.fim.raster_op;

import cz.uhk.fim.object_data.Point;
import cz.uhk.fim.raster_data.Polygon;
import cz.uhk.fim.raster_data.Raster;

import java.util.List;

/**
 * Class representing a drawer used to draw polygon onto image
 */
public class PolygonDrawer implements Drawer{

    /**
     * This method takes all points of a polygon and draws lines from the first point to the last one
     *
     * @param img     - buffered image to which the polygon should be drawn onto
     * @param liner   - Liner class that should be used to draw the polygon
     * @param polygon - polygon that will be drawn onto the image
     * @param color   - color of the polygon
     */
    @Override
    public void drawPolygon(Raster img, Liner liner, Polygon polygon, int color) {
        List<Point> points = polygon.getPoints();
        for (int i = 1; i < points.size(); i++) {
            liner.drawLine(img, points.get(i - 1).getX(), points.get(i - 1).getY(), points.get(i).getX(), points.get(i).getY(), color);
        }
        liner.drawLine(img, points.get(0).getX(), points.get(0).getY(), points.get(points.size() - 1).getX(), points.get(points.size() - 1).getY(), color);
    }

}
