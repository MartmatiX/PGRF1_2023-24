package cz.uhk.fim;

import cz.uhk.fim.constants.Constants;
import cz.uhk.fim.rasterdata.RasterBufferedImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Canvas {

    private final JFrame frame;
    private final JPanel panel;
    private final RasterBufferedImage img;

    private int cross_x;
    private int cross_y;

    private final int[] colors = {0xff0000, 0x00ff00, 0x0000ff};
    Random random = new Random();

    public Canvas(int width, int height) {
        frame = new JFrame();

        frame.setLayout(new BorderLayout());
        frame.setTitle("UHK FIM PGRF : " + this.getClass().getName());
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        img = new RasterBufferedImage(width, height);

        cross_x = img.getWidth() / 2;
        cross_y = img.getHeight() / 2;

        panel = new JPanel() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                img.present(g);
            }
        };

        panel.setPreferredSize(new Dimension(width, height));

        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                try{
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_W -> cross_y -= 1;
                        case KeyEvent.VK_S -> cross_y += 1;
                        case KeyEvent.VK_D -> cross_x += 1;
                        case KeyEvent.VK_A -> cross_x -= 1;
                    }
                    img.clear(Constants.DEFAULT_BACKGROUND_COLOR);
                    drawCross();
                    drawTrail();
                } catch (Exception ex) {
                    System.out.println("You are out of bounds!");
                }
            }
        });
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                img.setColor(e.getX(), e.getY(), 0xff0000);
                panel.repaint();
            }
        });
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                img.setColor(e.getX(), e.getY(), 0x00ff00);
                panel.repaint();
            }
        });
        panel.requestFocus();
    }

    private void drawCross() {
        img.setColor(cross_x, cross_y, 0xffff00);
        img.setColor(cross_x + 1, cross_y, 0xffff00);
        img.setColor(cross_x - 1, cross_y, 0xffff00);
        img.setColor(cross_x, cross_y + 1, 0xffff00);
        img.setColor(cross_x, cross_y - 1, 0xffff00);
    }

    private void drawTrail() {
        Point.trail.add(new Point(cross_x, cross_y, colors[random.nextInt(3)]));
        for (Point point : Point.trail) {
            img.setColor(point.x, point.y, point.color);
            panel.repaint();
        }
    }

    public void draw() {
        img.clear(Constants.DEFAULT_BACKGROUND_COLOR);
        img.setColor(400, 300, 0xffff00);
    }

    public void start() {
        draw();
        panel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Canvas(800, 600).start());
    }

    private static class Point {

        private final int x;
        private final int y;
        private final int color;

        protected static List<Point> trail = new ArrayList<>();

        public Point(int x, int y, int color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }

    }

}