package cz.uhk.fim.raster_data;

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

    public void addPoint(Point point) {
        this.points.add(point);
    }

    public List<Point> getPoints() {
        return this.points;
    }

}
