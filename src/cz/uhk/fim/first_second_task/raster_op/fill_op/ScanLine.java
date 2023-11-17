package cz.uhk.fim.first_second_task.raster_op.fill_op;

import cz.uhk.fim.first_second_task.object_data.Line;
import cz.uhk.fim.first_second_task.object_data.Point;
import cz.uhk.fim.first_second_task.raster_data.Polygon;
import cz.uhk.fim.first_second_task.raster_data.Raster;
import cz.uhk.fim.first_second_task.raster_op.Drawer;
import cz.uhk.fim.first_second_task.raster_op.Liner;
import cz.uhk.fim.first_second_task.utilities.Globals;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a Scan Line algorithm
 */
public class ScanLine {

    public void fill(Raster img, Polygon polygon, int fillColor, Drawer drawer, Liner liner) {
        List<Line> lines = polygon.getNonHorizontalLines();

        double yMin = polygon.getPoints().get(0).getY();
        double yMax = yMin;
        for (Point p : polygon.getPoints()) {
            if (p.getY() < yMin) yMin = p.getY();
            if (p.getY() > yMax) yMax = p.getY();
        }

        for (int y = (int) yMin; y <= yMax; y++) {
            List<Double> intersections = new ArrayList<>();

            for (Line line : lines) {
                if (line.hasYIntersection(y)) intersections.add(line.yIntercept(y));
            }

            sort(intersections);

            for (int i = 0; i < intersections.size() - 1; ) {
                double start = intersections.get(i++);
                double end = intersections.get(i++ % intersections.size());
                for (int x = (int) start; x < (int) end; x++) {
                    if (Globals.usePattern) {
                        int color = x % 8;
                        if (color == 1)
                            img.setColor(x, y, Globals.GREEN);
                        else
                            img.setColor(x, y, Globals.BLUE);
                    } else {
                        img.setColor(x, y, fillColor);
                    }
                }
            }
        }
        drawer.drawPolygon(img, liner, polygon, fillColor);
    }

    private void sort(List<Double> list) {
        for (int i = 0; i < list.size(); i++) {
            double min = list.get(i);
            int minId = i;
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(j) < min) {
                    min = list.get(j);
                    minId = j;
                }
            }
            double temp = list.get(i);
            list.set(i, min);
            list.set(minId, temp);
        }
    }

}
