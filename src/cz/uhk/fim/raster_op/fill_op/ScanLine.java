package cz.uhk.fim.raster_op.fill_op;

import cz.uhk.fim.raster_data.Polygon;
import cz.uhk.fim.raster_data.Raster;
import cz.uhk.fim.raster_op.Drawer;
import cz.uhk.fim.raster_op.Liner;

public class ScanLine {

    public void fill(Raster img, Polygon polygon, int fillColor, int edgeColor, Drawer drawer, Liner liner) {
        /*
        1. Create a list of lines
        2. Filter horizontal lines
        3. Orient lines in the list of lines

        4. Find yMin, yMax

        5. For r in rMin to rMax
            - create a list of intersections
            - for Line in the list of lines
                - find the intercept with r and save it to the list of intersections
            - sort the list of intercepts in ascending order
            draw vertical lines between even and odd intercepts
        6. Rasterize the polygon
         */
    }

}
