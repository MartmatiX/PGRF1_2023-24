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
        // TODO: 09.11.2023 This is probably not correct 
        Point t = new Point(p2.getX() - p1.getX(), p2.getY() - p1.getY());
        Point n = new Point(-t.getY(), t.getX());
        Point v = new Point(p.getX() - p1.getX(), p.getY() - p1.getY());

        double nLength = Math.sqrt(n.getX() * n.getX() + n.getY() * n.getY());
        double vLength = Math.sqrt(v.getX() * v.getX() + v.getY() * v.getY());

        Point xVector = new Point(n.getX() / nLength, n.getY() / nLength);
        Point yVector = new Point(v.getX() / vLength, v.getY() / vLength);

        double cosAlpha = xVector.getX() * yVector.getX() + xVector.getY() * yVector.getY();

        return cosAlpha >= 0;
    }

    public Point intersection(Point p1, Point p2, Point p3, Point p4) {
        // TODO: 09.11.2023 Finish this, it is probably not correct
        double x1 = p1.getX();
        double y1 = p1.getY();
        double x2 = p2.getX();
        double y2 = p2.getY();
        double x3 = p3.getX();
        double y3 = p3.getY();
        double x4 = p4.getX();
        double y4 = p4.getY();

        double x = (x1 * y2 - x2 * y1) * (x3 - x4) - (x3 * y4 - x4 * y3) * (x1 - x2) / (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        double y = (x1 * y2 - x2 * y1) * (y3 - y4) - (x3 * y4 - x4 * y3) * (y1 - y2) / (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);

        return new Point(x, y);
    }

}