package cz.uhk.fim.raster_op.fill_op;

import cz.uhk.fim.constants.Globals;
import cz.uhk.fim.object_data.Point;
import cz.uhk.fim.raster_data.Raster;

import java.util.Stack;
import java.util.function.Predicate;

/**
 * Class representing a Seed (Flood) fill 4 algorithm with the usage of custom stack (LIFO - Last In First Out).
 * The algorithm takes the 4 neighbours of the starting point and using recursive paints the pixels if they match the color of the background.
 * If you experience issues with heap space, increase Xss to 1024M (VM options -> -Xss1024M), in testing, this was enough to fill
 * rectangle with size of 1280x720.
 */
public class SeedFill4Stack implements SeedFill {

    private int index = 0;

    @Override
    public void fill(Raster img, int x, int y, int fillColor, Predicate<Integer> isInArea) {
        // Push a new Point to the top of the stack
        Stack<Point> stack = new Stack<>();
        stack.push(new Point(x, y));

        while (!stack.isEmpty()) {
            // Get the first Point and get its coordinates
            Point fromStack = stack.pop();
            final int currX = (int) fromStack.getX();
            final int currY = (int) fromStack.getY();

            img.getColor(currX, currY).ifPresentOrElse(color -> {
                if (isInArea.test(color) && currX < img.getWidth() && checkCoordinates(img, currX, currY)) {
                    if (Globals.usePattern) {
                        img.setColor(currX, currY, Globals.colors.get(index));
                        index++;
                        if (index > Globals.colors.size() - 1) index = 0;
                    } else {
                        img.setColor(currX, currY, fillColor);
                    }
                    fill(img, currX + 1, currY, fillColor, isInArea);
                    fill(img, currX - 1, currY, fillColor, isInArea);
                    fill(img, currX, currY + 1, fillColor, isInArea);
                    fill(img, currX, currY - 1, fillColor, isInArea);
                }
            }, () -> System.out.println("You are out of bounds!"));
        }
    }

}
