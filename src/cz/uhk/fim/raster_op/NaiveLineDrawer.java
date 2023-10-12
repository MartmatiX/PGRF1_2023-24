package cz.uhk.fim.raster_op;

import cz.uhk.fim.raster_data.Raster;

/**
 * Represents a naive algorithm for drawing lines
 */
public class NaiveLineDrawer implements Liner {

    @Override
    public void drawLine(Raster img, double x1, double y1, double x2, double y2, int color) {
        double k; // The angle of the line

        // Verify size difference of x1 and x2
        if (x1 != x2) {
            k = (y2 - y1) / (x2 - x1); // Calculate slope of the line, if the line is not vertical
        } else {
            /*
                Does not break if x1 == x2, but we lose accuracy
                Fixes vertical lines
             */
            k = Integer.MAX_VALUE;
        }

        final double q = y1 - k * x1;

        // Fix line for all quadrants
        if (Math.abs(y2 - y1) < Math.abs(x2 - x1)) {
            if (x1 > x2) { // Swap x1 and x2 to ensure correct values are fed to the loop
                double tmp = x1;
                x1 = x2;
                x2 = tmp;
            }

            // k < 1
            for (int x = (int) Math.round(x1); x < x2; x++) {
                double y = k * x + q;
                img.setColor(x, (int) y, color);
            }
        } else {
            if (y1 > y2) { // Swap y1 and y2 to ensure correct values are fed to the loop
                double tmp = y1;
                y1 = y2;
                y2 = tmp;
            }

            // k > 1
            for (int y = (int) y1; y <= y2; y++) { // Draw the line via y
                double x = Math.round((y - q) / k);
                img.setColor((int) x, y, color);
            }
        }
    }

}
