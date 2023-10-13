package cz.uhk.fim;

import cz.uhk.fim.constants.Globals;
import cz.uhk.fim.object_data.Point;
import cz.uhk.fim.raster_data.Polygon;
import cz.uhk.fim.raster_data.RasterBufferedImage;
import cz.uhk.fim.raster_op.DashedLineDrawer;
import cz.uhk.fim.raster_op.Liner;
import cz.uhk.fim.raster_op.NaiveLineDrawer;
import cz.uhk.fim.raster_op.PolygonDrawer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serial;

public class Canvas {

    private final JPanel panel;
    private final RasterBufferedImage img;

    private Liner liner = new NaiveLineDrawer();
    private int lineX;
    private int lineY;
    private int lineColor;

    private int flag;

    private final Polygon polygon = new Polygon();
    private final PolygonDrawer polygonDrawer = new PolygonDrawer();
    private String polygonMode = "";

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
                        img.clear(Globals.DEFAULT_BACKGROUND_COLOR);
                        System.out.println("Changed mode to Naive Line drawer");
                        liner = new NaiveLineDrawer();
                        lineColor = 0xff00ff;
                        flag = 1;
                    }
                    case KeyEvent.VK_2 -> {
                        img.clear(Globals.DEFAULT_BACKGROUND_COLOR);
                        System.out.println("Changed mode to Dashed Line drawer");
                        liner = new DashedLineDrawer();
                        lineColor = 0x00ffff;
                        flag = 2;
                    }
                    case KeyEvent.VK_3 -> {
                        img.clear(Globals.DEFAULT_BACKGROUND_COLOR);
                        flag = 3;
                        polygon.getPoints().clear();
                        System.out.println("Changed mode to Polygon drawer");
                        System.out.println("Please select liner [1 - Naive liner, 2 - Dashed liner]");
                        try {
                            polygonMode = bufferedReader.readLine().trim();
                            if (Integer.parseInt(polygonMode) == 1) {
                                liner = new NaiveLineDrawer();
                                System.out.println("Selected Naive liner");
                            } else if (Integer.parseInt(polygonMode) == 2) {
                                liner = new DashedLineDrawer();
                                System.out.println("Selected Dashed liner");
                            } else {
                                System.out.println("This number is not supported, using Naive liner");
                                liner = new NaiveLineDrawer();
                            }
                        } catch (Exception exception) {
                            liner = new NaiveLineDrawer();
                            System.out.println("Invalid option, using Naive liner");
                            System.out.println("Exception [" + exception + "]");
                        }
                    }
                    case KeyEvent.VK_C -> {
                        System.out.println("Clearing image...");
                        img.clear(Globals.DEFAULT_BACKGROUND_COLOR);
                        flag = 0;
                        Globals.setDefaultDashAndSpace();
                        System.out.println("Done");
                    }
                }

                if (flag == 2 || flag == 3 && polygonMode.equals("2") && e.getKeyCode() == KeyEvent.VK_S) {
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

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (flag == 3) {
                    img.clear(Globals.DEFAULT_BACKGROUND_COLOR);
                    polygon.addPoint(new Point(e.getX(), e.getY()));
                    System.out.println("New point added [" + e.getX() + ";" + e.getY() + "]");
                    if (polygon.getPoints().size() > 1) {
                        polygonDrawer.drawPolygon(img, liner, polygon, Globals.BLUE);
                        panel.repaint();
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
                    case 3 -> {
                        img.clear(Globals.DEFAULT_BACKGROUND_COLOR);
                        if (polygon.getPoints().size() > 1) {
                            polygonDrawer.drawPolygon(img, liner, polygon, 0x0000ff);
                            liner.drawLine(img, polygon.getPoints().get(0).getX(), polygon.getPoints().get(0).getY(), e.getX(), e.getY(), Globals.GREEN);
                            liner.drawLine(img, polygon.getPoints().get(polygon.getPoints().size() - 1).getX(), polygon.getPoints().get(polygon.getPoints().size() - 1).getY(), e.getX(), e.getY(), Globals.GREEN);
                            if (polygon.getPoints().size() > 2)
                                liner.drawLine(img, polygon.getPoints().get(0).getX(), polygon.getPoints().get(0).getY(), polygon.getPoints().get(polygon.getPoints().size() - 1).getX(), polygon.getPoints().get(polygon.getPoints().size() - 1).getY(), Globals.RED);
                        }
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