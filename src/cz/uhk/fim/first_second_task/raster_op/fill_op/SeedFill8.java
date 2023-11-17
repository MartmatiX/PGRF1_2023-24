package cz.uhk.fim.first_second_task.raster_op.fill_op;

import cz.uhk.fim.first_second_task.raster_data.Raster;

import java.util.function.Predicate;

/**
 * Class representing a Seed (Flood) fill 8 algorithm.
 * The algorithm takes the 8 neighbours of the starting point and using recursive paints the pixels if they match the color of the background.
 */
public class SeedFill8 implements SeedFill {

    @Override
    public void fill(Raster img, int x, int y, int fillColor, Predicate<Integer> isInArea) {
        img.getColor(x, y).ifPresentOrElse(color -> {
            if (isInArea.test(color) && x < img.getWidth() && checkCoordinates(img, x, y)) {
                img.setColor(x, y, color);
                fill(img, x + 1, y, fillColor, isInArea);
                fill(img, x - 1, y, fillColor, isInArea);
                fill(img, x, y + 1, fillColor, isInArea);
                fill(img, x, y - 1, fillColor, isInArea);

                fill(img, x + 1, y + 1, fillColor, isInArea);
                fill(img, x - 1, y - 1, fillColor, isInArea);
                fill(img, x - 1, y + 1, fillColor, isInArea);
                fill(img, x + 1, y - 1, fillColor, isInArea);
            }
        }, () -> System.out.println("You are out of bounds!"));
    }

}
