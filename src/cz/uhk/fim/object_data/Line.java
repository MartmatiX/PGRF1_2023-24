package cz.uhk.fim.object_data;

/**
 * Represents a line in a 2D space
 */
public class Line {

    private final Point p1;
    private final Point p2;

    public Line(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public boolean isHorizontal() {
        return p1.getY() == p2.getY();
    }

    public boolean hasYIntersection(double y) {
        return y >= p1.getY() && y < p2.getY();
    }

    public double yIntercept(double y) {
        double dx = p2.getX() - p1.getX();
        double dy = p2.getY() - p1.getY();
        double k = dx / dy;
        double q = p1.getX() - (k * p1.getY());
        double x = (k * y) + q;
        return Math.round(x);
    }

    public Line orientate() {
        if (p1.getY() >= p2.getY()) {
            return new Line(p2, p1);
        }
        return new Line(p1, p2);
    }

}
