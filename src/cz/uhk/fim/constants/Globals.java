package cz.uhk.fim.constants;

import java.util.List;

public class Globals {

    /**
     * All constants used across the application
     */

    // Default background color of the Buffered Image
    public static final int DEFAULT_BACKGROUND_COLOR = 0xff000000;

    // Set of colors that are used
    public static final int RED = 0xff0000;
    public static final int GREEN = 0x00ff00;
    public static final int BLUE = 0x0000ff;
    public static final int CYAN = 0x00ffff;
    public static final int PURPLE = 0xff00ff;

    public static boolean usePattern = false;
    public static List<Integer> colors = List.of(CYAN, BLUE);

    public static int spaceLength = 1;
    public static int dashLength = 1;

    public static void setDefaultDashAndSpace() {
        spaceLength = 1;
        dashLength = 1;
    }
}
