package cz.uhk.fim.raster_data;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Optional;

public class RasterBufferedImage implements Raster, Presentable {

    private final BufferedImage img;

    public RasterBufferedImage(int width, int height) {
        this.img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    @Override
    public int getWidth() {
        return img.getWidth();
    }

    @Override
    public int getHeight() {
        return img.getHeight();
    }

    @Override
    public void setColor(int x, int y, int color) {
        if (x > img.getWidth() - 1 || x < 0 || y > img.getHeight() - 1 || y < 0) {
            return;
        }
        img.setRGB(x, y, color);
    }

    @Override
    public Optional<Integer> getColor(int x, int y) {
        if (x > img.getWidth() - 1 || x < 0 || y > img.getHeight() - 1 || y < 0) {
            return Optional.empty();
        }
        return Optional.of(img.getRGB(x, y));
    }

    @Override
    public void clear(int background) {
        Graphics gr = img.getGraphics();
        gr.setColor(new Color(background));
        gr.fillRect(0, 0, img.getWidth(), img.getHeight());
    }

    @Override
    public void present(Graphics graphics) {
        graphics.drawImage(img, 0, 0, null);
    }

    @Override
    public Graphics getGraphics() {
        return img.getGraphics();
    }

    public BufferedImage getImg() {
        return img;
    }
}
