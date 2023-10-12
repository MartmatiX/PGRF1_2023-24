package cz.uhk.fim;

import cz.uhk.fim.constants.Globals;
import cz.uhk.fim.raster_data.RasterBufferedImage;
import cz.uhk.fim.raster_op.DashedLineDrawer;
import cz.uhk.fim.raster_op.Liner;
import cz.uhk.fim.raster_op.NaiveLineDrawer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serial;

public class Canvas {

    private final JPanel panel;
    private final RasterBufferedImage img;

    private Liner liner;
    private int lineX;
    private int lineY;
    private int lineColor;

    private int flag;

    private final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    public Canvas(int width, int height) {
        JFrame frame = new JFrame();

        frame.setLayout(new BorderLayout());
        frame.setTitle("UHK FIM PGRF : " + this.getClass().getName());
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        img = new RasterBufferedImage(width, height);

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
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ESCAPE -> {
                        System.out.println("Exiting application");
                        System.exit(0);
                    }
                    case KeyEvent.VK_1 -> {
                        System.out.println("Changed mode to Naive Line drawer");
                        liner = new NaiveLineDrawer();
                        lineColor = 0xff00ff;
                        flag = 1;
                    }
                    case KeyEvent.VK_2 -> {
                        System.out.println("Changed mode to Dashed Line drawer");
                        liner = new DashedLineDrawer();
                        lineColor = 0x00ffff;
                        flag = 2;
                    }
                    case KeyEvent.VK_C -> {
                        System.out.println("Clearing image...");
                        img.clear(Globals.DEFAULT_BACKGROUND_COLOR);
                        flag = 0;
                        Globals.setDefaultDashAndSpace();
                        panel.repaint();
                        System.out.println("Done");
                    }
                }

                if (flag == 2 && e.getKeyCode() == KeyEvent.VK_S) {
                    try {
                        System.out.println("Enter new length for space between pixels:");
                        Globals.spaceLength = Integer.parseInt(bufferedReader.readLine());
                        System.out.println("Enter new length for length of each dash:");
                        Globals.dashLength = Integer.parseInt(bufferedReader.readLine());
                        System.out.println("Dash size changed to [" + Globals.dashLength + "] and space between lines changed to [" + Globals.spaceLength + "]");
                    } catch (Exception exception) {
                        Globals.setDefaultDashAndSpace();
                        System.out.println("Exception occurred. Setting values back to default!");
                        System.out.println("Stack trace [" + exception + "]");
                    }
                }
                panel.repaint();
                img.clear(Globals.DEFAULT_BACKGROUND_COLOR);
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                switch (flag) {
                    case 1, 2 -> {
                        lineX = e.getX();
                        lineY = e.getY();
                        prepareLineStart(lineX, lineY, lineColor);
                    }
                }
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                switch (flag) {
                    case 1, 2 -> {
                        img.clear(Globals.DEFAULT_BACKGROUND_COLOR);
                        liner.drawLine(img, lineX, lineY, e.getX(), e.getY(), lineColor);
                        panel.repaint();
                    }
                }
            }
        });
        panel.requestFocus();
    }

    private void prepareLineStart(int x, int y, int color) {
        img.setColor(x, y, color);
        panel.repaint();
        System.out.println("Selected new starting point [" + lineX + ";" + lineY + "]");
    }

    public void draw() {
        img.clear(Globals.DEFAULT_BACKGROUND_COLOR);
    }

    public void start() {
        draw();
        panel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Canvas(1280, 720).start());
    }

}