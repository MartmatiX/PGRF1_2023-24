package cz.uhk.fim.raster_data;

import cz.uhk.fim.object_data.Line;
import cz.uhk.fim.object_data.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a Polygon that consists of a number of Points
 */

public class Polygon {

    private final List<Point> points = new ArrayList<>();

    public Polygon() {
    }

    public Polygon(Polygon polygon) {
        this.points.addAll(polygon.getPoints());
    }

    public void addPoint(Point point) {
        this.points.add(point);
    }

    public List<Point> getPoints() {
        return this.points;
    }

    public List<Line> getNonHorizontalLines() {
        List<Line> lines = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            int nextIndex = (i + 1) % points.size();
            Point p1 = points.get(i);
            Point p2 = points.get(nextIndex);
            Line line = new Line(p1, p2);
            if (line.isHorizontal()) continue;
            Line orientated = line.orientate();
            lines.add(orientated);
        }
        return lines;
    }

}
