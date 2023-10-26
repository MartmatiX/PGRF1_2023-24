package cz.uhk.fim;

import cz.uhk.fim.constants.Globals;
import cz.uhk.fim.object_data.Point;
import cz.uhk.fim.raster_data.Polygon;
import cz.uhk.fim.raster_data.RasterBufferedImage;
import cz.uhk.fim.raster_op.DashedLineDrawer;
import cz.uhk.fim.raster_op.Liner;
import cz.uhk.fim.raster_op.NaiveLineDrawer;
import cz.uhk.fim.raster_op.PolygonDrawer;
import cz.uhk.fim.raster_op.fill_op.SeedFill;
import cz.uhk.fim.raster_op.fill_op.SeedFill4;

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
    private boolean polygonRemovePointFlag = true;
    private Point closest = null;

    private boolean shiftDown = false;

    SeedFill seedFill = new SeedFill4();
    private int fillColor = Globals.CYAN;
    private boolean controlDown = false;

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
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
        panel.add(initTextArea(), BorderLayout.WEST);
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
                        lineColor = Globals.PURPLE;
                        flag = 1;
                    }
                    case KeyEvent.VK_2 -> {
                        img.clear(Globals.DEFAULT_BACKGROUND_COLOR);
                        System.out.println("Changed mode to Dashed Line drawer");
                        liner = new DashedLineDrawer();
                        lineColor = Globals.CYAN;
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
                    case KeyEvent.VK_4 -> {
                        System.out.println("Changed to SeedFill4 mode");
                        flag = 4;
                    }
                    case KeyEvent.VK_SHIFT -> shiftDown = true;
                    case KeyEvent.VK_CONTROL -> controlDown = true;
                    case KeyEvent.VK_R -> {
                        if (flag == 3) {
                            if (polygonRemovePointFlag) System.out.println("Swapped to edit mode");
                            else System.out.println("Swapped to delete mode");
                            polygonRemovePointFlag = !polygonRemovePointFlag;
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

                if (flag == 2 && e.getKeyCode() == KeyEvent.VK_S) {
                    try {
                        System.out.println("Enter new value for space between pixels:");
                        Globals.spaceLength = Integer.parseInt(bufferedReader.readLine());
                        System.out.println("Enter new value for length of each dash:");
                        Globals.dashLength = Integer.parseInt(bufferedReader.readLine());
                        System.out.println("Dash size changed to [" + Globals.dashLength + "] and space between lines changed to [" + Globals.spaceLength + "]");
                    } catch (Exception exception) {
                        Globals.setDefaultDashAndSpace();
                        System.out.println("Exception occurred. Setting values back to default!");
                        System.out.println("Stack trace [" + exception + "]");
                    }
                }
                if (flag == 3 && e.getKeyCode() == KeyEvent.VK_S) {
                    try {
                        System.out.println("Select different Seed (Flood) Fill color");
                        System.out.println("""
                                1 - Red
                                2 - Green
                                3 - Blue
                                4 - Cyan
                                5 - Purple
                                6 - Pattern
                                """);
                        switch (Integer.parseInt(bufferedReader.readLine())) {
                            case 1 -> {
                                fillColor = Globals.RED;
                                Globals.usePattern = false;
                            }
                            case 2 -> {
                                fillColor = Globals.GREEN;
                                Globals.usePattern = false;
                            }
                            case 3 -> {
                                fillColor = Globals.BLUE;
                                Globals.usePattern = false;
                            }
                            case 4 -> {
                                fillColor = Globals.CYAN;
                                Globals.usePattern = false;
                            }
                            case 5 -> {
                                fillColor = Globals.PURPLE;
                                Globals.usePattern = false;
                            }
                            case 6 -> Globals.usePattern = !Globals.usePattern;
                        }
                    } catch (Exception exception) {
                        fillColor = Globals.CYAN;
                        System.out.println("Exception occurred. Setting color back to default (Cyan)!");
                        System.out.println("Stack trace [" + exception + "]");
                    }
                }
                panel.repaint();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SHIFT -> shiftDown = false;
                    case KeyEvent.VK_CONTROL -> controlDown = false;
                }
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                switch (flag) {
                    case 1, 2 -> {
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            lineX = e.getX();
                            lineY = e.getY();
                            prepareLineStart(lineX, lineY, lineColor);
                        }
                    }
                    case 3 -> {
                        if (e.getButton() == MouseEvent.BUTTON3) {
                            double closestDistance = Double.MAX_VALUE;

                            // Loop across all points and find the closest one to e.getX() and e.getY()
                            for (Point point : polygon.getPoints()) {
                                double distance = Math.sqrt(Math.pow(point.getX() - e.getX(), 2) + Math.pow(point.getY() - e.getY(), 2)); // Euclidean distance formula
                                if (distance < closestDistance) {
                                    closestDistance = distance;
                                    closest = point;
                                }
                            }
                            // Remove the closest point and redraw the polygon
                            if (polygonRemovePointFlag && polygon.getPoints().size() > 1) {
                                polygon.getPoints().remove(closest);
                                img.clear(Globals.DEFAULT_BACKGROUND_COLOR);
                                polygonDrawer.drawPolygon(img, liner, polygon, Globals.BLUE);
                                System.out.println("Removed Point located at [" + closest.getX() + ";" + closest.getY() + "]");
                                panel.repaint();
                            }
                        }
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                if (flag == 3 && e.getButton() == MouseEvent.BUTTON3) {
                    double closestDistance = Double.MAX_VALUE;

                    for (Point point : polygon.getPoints()) {
                        double distance = Math.sqrt(Math.pow(point.getX() - e.getX(), 2) + Math.pow(point.getY() - e.getY(), 2)); // Euclidean distance formula
                        if (distance < closestDistance) {
                            closestDistance = distance;
                            closest = point;
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (flag == 3 && e.getButton() == MouseEvent.BUTTON1 && !controlDown) {
                    // Add point only when mouse is released, so we can draw lines via mouseDragged
                    img.clear(Globals.DEFAULT_BACKGROUND_COLOR);
                    polygon.addPoint(new Point(e.getX(), e.getY()));
                    System.out.println("New point added [" + e.getX() + ";" + e.getY() + "]");
                    if (polygon.getPoints().size() > 1) {
                        polygonDrawer.drawPolygon(img, liner, polygon, Globals.BLUE);
                        panel.repaint();
                    }
                } else if (flag == 3 && e.getButton() == MouseEvent.BUTTON3 && !polygonRemovePointFlag && !controlDown) {
                    closest.setX(e.getX());
                    closest.setY(e.getY());
                    System.out.println("Updated coordinates of point to [" + e.getX() + ";" + e.getY() + "]");
                    img.clear(Globals.DEFAULT_BACKGROUND_COLOR);
                    polygonDrawer.drawPolygon(img, liner, polygon, Globals.BLUE);
                    panel.repaint();
                } else if (flag == 3 && e.getButton() == MouseEvent.BUTTON1 && controlDown && Integer.parseInt(polygonMode) == 1) {
                    seedFill.fill(img, e.getX(), e.getY(), fillColor, color -> color == Globals.DEFAULT_BACKGROUND_COLOR);
                    panel.repaint();
                }
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                switch (flag) {
                    case 1, 2 -> {
                        if (shiftDown) {
                            img.clear(Globals.DEFAULT_BACKGROUND_COLOR);

                            int deltaX = Math.abs(e.getX() - lineX); // Horizontal distance between X and e.getX()
                            int deltaY = Math.abs(e.getY() - lineY); // Vertical distance between Y and e.getY()

                            if (deltaX > deltaY) {
                                liner.drawLine(img, lineX, lineY, e.getX(), lineY, lineColor); // If delta on X axis is bigger, draw horizontal line
                            } else if (deltaY > deltaX) {
                                liner.drawLine(img, lineX, lineY, lineX, e.getY(), lineColor); // If delta on Y axis is bigger, draw vertical line
                            } else {
                                liner.drawLine(img, lineX, lineY, e.getX(), e.getY(), lineColor); // If neither of the above apply, draw diagonal
                            }
                        } else {
                            img.clear(Globals.DEFAULT_BACKGROUND_COLOR);
                            liner.drawLine(img, lineX, lineY, e.getX(), e.getY(), lineColor);
                        }
                        panel.repaint();
                    }
                    case 3 -> {
                        img.clear(Globals.DEFAULT_BACKGROUND_COLOR);
                        polygonDrawer.drawPolygon(img, liner, polygon, Globals.BLUE);
                        if (polygon.getPoints().size() > 1 && polygonRemovePointFlag && SwingUtilities.isLeftMouseButton(e)) {
                            drawLeadingLines(e);
                        } else if (!polygonRemovePointFlag && SwingUtilities.isRightMouseButton(e)) {
                            if (closest != null) {
                                liner.drawLine(img, closest.getX(), closest.getY(), e.getX(), e.getY(), Globals.GREEN);
                            }
                        } else if (!polygonRemovePointFlag && SwingUtilities.isLeftMouseButton(e)) {
                            drawLeadingLines(e);
                        }
                        panel.repaint();
                    }
                }
            }

        });
        panel.requestFocus();
    }

    public void drawLeadingLines(MouseEvent e) {
        // Draw lines from first and last point to create interactive feeling
        liner.drawLine(img, polygon.getPoints().get(0).getX(), polygon.getPoints().get(0).getY(), e.getX(), e.getY(), Globals.GREEN);
        liner.drawLine(img, polygon.getPoints().get(polygon.getPoints().size() - 1).getX(), polygon.getPoints().get(polygon.getPoints().size() - 1).getY(), e.getX(), e.getY(), Globals.GREEN);
        if (polygon.getPoints().size() > 2)
            // If the array size is larger than 2, paint in red the line that will be deleted
            liner.drawLine(img, polygon.getPoints().get(0).getX(), polygon.getPoints().get(0).getY(), polygon.getPoints().get(polygon.getPoints().size() - 1).getX(), polygon.getPoints().get(polygon.getPoints().size() - 1).getY(), Globals.RED);
    }

    public JTextArea initTextArea() {
        JTextArea textArea = new JTextArea();
        textArea.setForeground(new Color(255, 255, 255));
        textArea.setFocusable(false);
        textArea.setEditable(false);
        textArea.setBackground(Color.BLACK);
        String text = """
                Welcome to the Application!
                Please select mode:
                            
                1 - Naive line drawer
                2 - Dashed line drawer
                    'S' key to change the spacing and length of each dash.
                    Follow instruction in the terminal.
                3 - Polygon drawer
                    Before using, please, select mode in the terminal.
                    RMB to delete point
                        Press 'R' to swap between remove and edit mode
                    CTRL to seed fill the polygon (you have to use naive line mode, not dashed line mode)
                    'S' key to change the color of the Seed (Flood) Fill algorithm
                ESC - Exit
                """;
        textArea.setText(text);
        return textArea;
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