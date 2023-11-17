package cz.uhk.fim.first_second_task.raster_op;

import cz.uhk.fim.first_second_task.raster_data.Polygon;
import cz.uhk.fim.first_second_task.raster_data.Raster;

/**
 * Class representing a drawer used to draw polygons onto image
 */
public interface Drawer {

    /**
     * @param img     - buffered image to which the polygon should be drawn onto
     * @param liner   - Liner class that should be used to draw the polygon
     * @param polygon - polygon that will be drawn onto the image
     * @param color   - color of the polygon
     */
    void drawPolygon(Raster img, Liner liner, Polygon polygon, int color);

}
