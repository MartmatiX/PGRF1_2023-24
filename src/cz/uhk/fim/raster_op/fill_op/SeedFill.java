package cz.uhk.fim.raster_op.fill_op;

import cz.uhk.fim.raster_data.Raster;

import java.util.function.Predicate;

public interface SeedFill {

    void fill(Raster img, int x, int y, int fillColor, Predicate<Integer> isInArea);

    /**
     * Checks whether the given coordinates are inside the image
     *
     * @param img - image on which the values should be tested
     * @param x   - x coordinate of the starting Point
     * @param y   - y coordinate of the starting Point
     * @return - return true if the coordinate is within the image, false otherwise
     */
    default boolean checkCoordinates(Raster img, int x, int y) {
        return x < img.getWidth() - 1 && x > 0 && y < img.getHeight() - 1 && y > 0;
    }

}
