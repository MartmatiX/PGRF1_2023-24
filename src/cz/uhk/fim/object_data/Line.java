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

    public boolean isInside(Point p) {
        double side = ((p2.getX() - p1.getX()) * (p.getY() - p1.getY()) - (p2.getY() - p1.getY()) * (p.getX() - p1.getX()));
        return side < 0;
    }

    public Point intersection(Point v1, Point v2) {
        double x0, y0;
        double x3 = v2.getX();
        double y3 = v2.getY();
        double x4 = v1.getX();
        double y4 = v1.getY();

        double v = (p1.getX() - p2.getX()) * (y3 - y4) - (p1.getY() - p2.getY()) * (x3 - x4);
        x0 = ((p1.getX() * p2.getY() - p2.getX() * p1.getY()) * (x3 - x4) - (x3 * y4 - x4 * y3) * (p1.getX() - p2.getX())) / v;
        y0 = ((p1.getX() * p2.getY() - p2.getX() * p1.getY()) * (y3 - y4) - (x3 * y4 - x4 * y3) * (p1.getY() - p2.getY())) / v;
        return new Point(Math.round(x0), Math.round(y0));
    }

}