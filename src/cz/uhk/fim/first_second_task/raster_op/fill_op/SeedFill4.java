package cz.uhk.fim.first_second_task.raster_op.fill_op;

import cz.uhk.fim.first_second_task.utilities.Globals;
import cz.uhk.fim.first_second_task.raster_data.Raster;

import java.util.function.Predicate;

/**
 * Class representing a Seed (Flood) fill 4 algorithm.
 * The algorithm takes the 4 neighbours of the starting point and using recursive paints the pixels if they match the color of the background.
 * If you experience issues with heap space, increase Xss to 1024M (VM options -> -Xss1024M), in testing, this was enough to fill
 * rectangle with size of 1280x720.
 */
public class SeedFill4 implements SeedFill {

    @Override
    public void fill(Raster img, int x, int y, int fillColor, Predicate<Integer> isInArea) {
        img.getColor(x, y).ifPresentOrElse(color -> {
            if (isInArea.test(color) && x < img.getWidth() && checkCoordinates(img, x, y)) {
                if (Globals.usePattern) {
                    int colorSelector = x % 16;
                    if (colorSelector == 0) img.setColor(x, y, Globals.RED);
                    else img.setColor(x, y, Globals.CYAN);
                } else {
                    img.setColor(x, y, fillColor);
                }
                fill(img, x + 1, y, fillColor, isInArea);
                fill(img, x - 1, y, fillColor, isInArea);
                fill(img, x, y + 1, fillColor, isInArea);
                fill(img, x, y - 1, fillColor, isInArea);
            }
        }, () -> System.out.println("You are out of bounds!"));
    }

}
