package cz.uhk.fim;

import cz.uhk.fim.raster_data.RasterBufferedImage;

import javax.swing.*;
import java.awt.*;

public class Space {

    private final JPanel panel;
    private final RasterBufferedImage raster;

    public Space(int width, int height) {
        JFrame frame = new JFrame();

        frame.setLayout(new BorderLayout());
        frame.setTitle("PGRF1 Malir");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        raster = new RasterBufferedImage(width, height);

        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                raster.present(g);
            }
        };

        panel.setPreferredSize(new Dimension(width, height));
    }

}
