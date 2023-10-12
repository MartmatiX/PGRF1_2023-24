package cz.uhk.fim.raster_op;

import cz.uhk.fim.constants.Globals;
import cz.uhk.fim.raster_data.Raster;

/**
 * This class represents a dashed line that uses Naive Line algorithm to draw a line with added spacing between pixels to create feeling of dashed line being drawn
 * The lines are being drawn until certain set limit is reached, in default 5, 5. Meaning the dash will be drawn for 5 pixels then there will be 5 pixel delay.
 * i.e. - represents dash, * represents space -> with limit 5, 5 -> -----*****-----*****-----*****
 */
public class DashedLineDrawer implements Liner {

    @Override
    public void drawLine(Raster img, double x1, double y1, double x2, double y2, int color) {
        int space = 0; // Track space between the lines
        int length = 0; // Track length of the lines

        double k;

        if (x1 != x2) {
            k = (y2 - y1) / (x2 - x1);
        } else {
            k = Integer.MAX_VALUE;
        }

        final double q = y1 - k * x1;

        if (Math.abs(y2 - y1) < Math.abs(x2 - x1)) {
            if (x1 > x2) {
                double tmp = x1;
                x1 = x2;
                x2 = tmp;
            }

            for (int x = (int) Math.round(x1); x < x2; x++) {
                if (space > Globals.spaceLength) { // If the space length exceeds the set size reset counters
                    length = 0;
                    space = 0;
                }
                if (length <= Globals.dashLength) {
                    double y = k * x + q;
                    img.setColor(x, (int) y, color);
                    length++;
                } else {
                    space++;
                }
            }
        } else {
            if (y1 > y2) {
                double tmp = y1;
                y1 = y2;
                y2 = tmp;
            }

            for (int y = (int) y1; y <= y2; y++) {
                if (space > Globals.spaceLength) { // If the space length exceeds the set size reset counters
                    length = 0;
                    space = 0;
                }
                if (length <= Globals.dashLength) {
                    double x = Math.round((y - q) / k);
                    img.setColor((int) x, y, color);
                    length++;
                } else {
                    space++;
                }
            }
        }
    }

}
