package cz.uhk.fim.raster_op;

import cz.uhk.fim.object_data.Point;
import cz.uhk.fim.raster_data.Polygon;
import cz.uhk.fim.raster_data.Raster;

/**
 * Class representing a drawer used to draw ellipse onto an image
 */
public class EllipseDrawer implements Drawer {

    @Override
    public void drawPolygon(Raster img, Liner liner, Polygon rectangle, int color) {
        Point p1 = rectangle.getPoints().get(0);
        Point p2 = rectangle.getPoints().get(1);

        Point center = new Point((p1.getX() + p2.getX()) / 2, (p1.getY() + p2.getY()) / 2);

        double width = Math.abs(p2.getX() - p1.getX());
        double height = Math.abs(p2.getY() - p1.getY());

        Point top = new Point(center.getX(), center.getY() - height / 2);
        Point bottom = new Point(center.getX(), center.getY() + height / 2);
        Point left = new Point(center.getX() - width / 2, center.getY());
        Point right = new Point(center.getX() + width / 2, center.getY());

        liner.drawLine(img, center, top, color);
        liner.drawLine(img, center, bottom, color);
        liner.drawLine(img, center, left, color);
        liner.drawLine(img, center, right, color);

        int numSegments = 360000;

        for (int i = 0; i < numSegments; i++) {
            double angle = 2 * Math.PI * i / numSegments;
            double x = center.getX() + (width / 2) * Math.cos(angle);
            double y = center.getY() + (height / 2) * Math.sin(angle);

            Point pointOnEllipse = new Point(x, y);

            liner.drawPixel(img, pointOnEllipse, color);
        }
    }
}
