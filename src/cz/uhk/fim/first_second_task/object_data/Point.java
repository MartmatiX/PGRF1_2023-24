package cz.uhk.fim.first_second_task.object_data;

/**
 * Represents a point in a 2D space
 */
public class Point {

    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }
}
