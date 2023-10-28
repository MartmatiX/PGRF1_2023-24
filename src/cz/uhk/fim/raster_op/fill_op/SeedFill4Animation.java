package cz.uhk.fim.raster_op.fill_op;

import cz.uhk.fim.constants.Globals;
import cz.uhk.fim.raster_data.Raster;

import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Predicate;

/**
 * This class represents a Seed (Flood) Fill algorithm that shows how the polygon is being filled.
 */
public class SeedFill4Animation implements SeedFill {

    private final JPanel panel;
    private int index = 0;
    Timer timer = new Timer();

    public SeedFill4Animation(JPanel panel) {
        this.panel = panel;
    }

    @Override
    public void fill(Raster img, int x, int y, int fillColor, Predicate<Integer> isInArea) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                img.getColor(x, y).ifPresentOrElse(color -> {
                    if (isInArea.test(color) && x < img.getWidth() && checkCoordinates(img, x, y)) {
                        if (Globals.usePattern) {
                            img.setColor(x, y, Globals.colors.get(index));
                            index++;
                            if (index > Globals.colors.size() - 1) index = 0;
                        } else {
                            img.setColor(x, y, fillColor);
                        }
                        panel.repaint();
                        fill(img, x + 1, y, fillColor, isInArea);
                        fill(img, x - 1, y, fillColor, isInArea);
                        fill(img, x, y + 1, fillColor, isInArea);
                        fill(img, x, y - 1, fillColor, isInArea);
                    }
                }, () -> System.out.println("You are out of bounds!"));
            }
        }, 3);
    }

}
