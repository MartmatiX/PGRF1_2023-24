package cz.uhk.fim.raster_data;

import java.awt.*;

public interface Presentable {

    /**
     * Presents an image on the screen
     *
     * @param graphics - to which graphics the image should render to
     */
    void present(Graphics graphics);

}
