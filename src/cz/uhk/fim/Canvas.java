package cz.uhk.fim;

import cz.uhk.fim.object_ops.PolygonCutter;
import cz.uhk.fim.raster_op.*;
import cz.uhk.fim.raster_op.fill_op.*;
import cz.uhk.fim.utilities.Globals;
import cz.uhk.fim.object_data.Point;
import cz.uhk.fim.raster_data.Polygon;
import cz.uhk.fim.raster_data.RasterBufferedImage;
import cz.uhk.fim.raster_data.Rectangle;

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

    private Drawer drawer;

    private final Polygon polygon = new Polygon();
    private final Polygon croppingPolygon = new Polygon();
    private boolean polygonSwitch = true;
    private String polygonMode = "";
    private boolean polygonRemovePointFlag = true;
    private Point closest = null;

    private boolean shiftDown = false;

    private SeedFill seedFill = new SeedFill4();
    private int fillColor = Globals.CYAN;
    private boolean controlDown = false;
    private boolean ownStack = false;

    private final Rectangle rectangle = new Rectangle();

    private final ScanLine scanLine = new ScanLine();
    private int scanLineColor = Globals.CYAN;

    private final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    private int mouseX;
    private int mouseY;
    private JTextArea textArea;

    private JLabel statusLabel;
    private String statusMessage = "Welcome!";

    private JTextField spaceInput;
    private JTextField lengthInput;

    public Canvas(int width, int height) {
        JFrame frame = new JFrame();

        frame.setLayout(new BorderLayout());
        frame.setTitle("PGRF1 Malir - task 1 and 2");
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
        frame.add(initGui(), BorderLayout.EAST);
        frame.pack();
        frame.setVisible(true);

        start();

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ESCAPE -> exitApplication();
                    case KeyEvent.VK_1 -> initNaiveLine();
                    case KeyEvent.VK_2 -> initDashedLine();
                    case KeyEvent.VK_3 -> initPolygonMode();
                    case KeyEvent.VK_4 -> initRectangleMode();
                    case KeyEvent.VK_SHIFT -> shiftDown = true;
                    case KeyEvent.VK_CONTROL -> controlDown = true;
                    case KeyEvent.VK_R -> swapEditAndDeleteMode();
                    case KeyEvent.VK_C -> clearCanvas();
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
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    initChangeColorSequence();
                }
                if (e.getKeyCode() == KeyEvent.VK_K) {
                    swapOwnStackOrJava();
                }
                if (e.getKeyCode() == KeyEvent.VK_M) {
                    switchToAnimationFill();
                }
                if (e.getKeyCode() == KeyEvent.VK_E) {
                    drawEllipseInsideRectangle();
                }
                if (e.getKeyCode() == KeyEvent.VK_F) {
                    scanLineFill();
                }
                if (e.getKeyCode() == KeyEvent.VK_W) {
                    changeActivePolygon();
                }
                if (e.getKeyCode() == KeyEvent.VK_Q) {
                    fillCroppedArea();
                }
                if (e.getKeyCode() == KeyEvent.VK_N) {
                    createNewCropper();
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

                            // Loop across all points and find the closest one to e.getX() and e.getY()
                            if (polygonSwitch) calculateClosestForPolygon(polygon, e);
                            else calculateClosestForPolygon(croppingPolygon, e);
                            // Remove the closest point and redraw the polygon
                            if (polygonRemovePointFlag && polygon.getPoints().size() > 1 && polygonSwitch) {
                                removeAndRepaintPolygon(polygon);
                            }
                            if (polygonRemovePointFlag && croppingPolygon.getPoints().size() > 1 && !polygonSwitch)
                                removeAndRepaintPolygon(croppingPolygon);
                        }
                    }
                    case 4 -> {
                        if (e.getButton() == MouseEvent.BUTTON1 && rectangle.getPoints().size() < 2) {
                            prepareLineStart(e.getX(), e.getY(), Globals.PURPLE);
                            rectangle.addPoint(new Point(e.getX(), e.getY()));
                        }
                        if (rectangle.getPoints().size() == 2) {
                            drawer.drawPolygon(img, liner, rectangle, Globals.PURPLE);
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
                if (flag == 3 && e.getButton() == MouseEvent.BUTTON1 && !controlDown && polygonSwitch) {
                    // Add point only when mouse is released, so we can draw lines via mouseDragged
                    img.clear(Globals.DEFAULT_BACKGROUND_COLOR);
                    polygon.addPoint(new Point(e.getX(), e.getY()));
                    System.out.println("New point added [" + e.getX() + ";" + e.getY() + "]");
                    if (polygon.getPoints().size() > 1) {
                        drawer.drawPolygon(img, liner, polygon, Globals.BLUE);
                        panel.repaint();
                    }
                } else if (flag == 3 && e.getButton() == MouseEvent.BUTTON3 && !polygonRemovePointFlag && !controlDown) {
                    recalculateClosestForCurrentPolygon(e);
                    closest.setX(e.getX());
                    closest.setY(e.getY());
                    System.out.println("Updated coordinates of point to [" + e.getX() + ";" + e.getY() + "]");
                    img.clear(Globals.DEFAULT_BACKGROUND_COLOR);
                    checkPolygonSizeAndDraw();
                } else if (flag == 3 && e.getButton() == MouseEvent.BUTTON1 && controlDown && Integer.parseInt(polygonMode) == 1) {
                    seedFill.fill(img, e.getX(), e.getY(), fillColor, color -> color == Globals.DEFAULT_BACKGROUND_COLOR);
                    panel.repaint();
                } else if (flag == 4 && rectangle.getPoints().size() == 2 && controlDown) {
                    seedFill.fill(img, e.getX(), e.getY(), fillColor, color -> color == Globals.DEFAULT_BACKGROUND_COLOR);
                    panel.repaint();
                }
                if (flag == 3 && e.getButton() == MouseEvent.BUTTON1 && !controlDown && !polygonSwitch) {
                    // Add point only when mouse is released, so we can draw lines via mouseDragged
                    img.clear(Globals.DEFAULT_BACKGROUND_COLOR);
                    croppingPolygon.addPoint(new Point(e.getX(), e.getY()));
                    System.out.println("New point added [" + e.getX() + ";" + e.getY() + "]");
                    if (croppingPolygon.getPoints().size() > 1) {
                        drawer.drawPolygon(img, liner, croppingPolygon, Globals.CYAN);
                        if (polygon.getPoints().size() > 1) drawer.drawPolygon(img, liner, polygon, Globals.BLUE);
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
                        checkPolygonSizeAndDraw();
                        if (polygon.getPoints().size() > 1 && polygonRemovePointFlag && SwingUtilities.isLeftMouseButton(e)) {
                            if (polygonSwitch) drawLeadingLines(e, polygon);
                            else if (croppingPolygon.getPoints().size() > 1) drawLeadingLines(e, croppingPolygon);
                        } else if (!polygonRemovePointFlag && SwingUtilities.isRightMouseButton(e)) {
                            recalculateClosestForCurrentPolygon(e);
                            if (closest != null) {
                                liner.drawLine(img, closest.getX(), closest.getY(), e.getX(), e.getY(), Globals.GREEN);
                            }
                        } else if (!polygonRemovePointFlag && SwingUtilities.isLeftMouseButton(e)) {
                            if (polygonSwitch) drawLeadingLines(e, polygon);
                            else drawLeadingLines(e, croppingPolygon);
                        }
                        panel.repaint();
                    }
                }
                mouseX = e.getX();
                mouseY = e.getY();
                updateText();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                mouseX = e.getX();
                mouseY = e.getY();
                updateText();
            }
        });
        panel.requestFocus();
    }

    private void recalculateClosestForCurrentPolygon(MouseEvent e) {
        if (polygonSwitch) {
            calculateClosestForPolygon(polygon, e);
        } else {
            calculateClosestForPolygon(croppingPolygon, e);
        }
    }

    private void calculateClosestForPolygon(Polygon polygon, MouseEvent e) {
        double closestDistance = Double.MAX_VALUE;
        for (Point point : polygon.getPoints()) {
            double distance = Math.sqrt(Math.pow(point.getX() - e.getX(), 2) + Math.pow(point.getY() - e.getY(), 2)); // Euclidean distance formula
            if (distance < closestDistance) {
                closestDistance = distance;
                closest = point;
            }
        }
    }

    private void removeAndRepaintPolygon(Polygon polygon) {
        polygon.getPoints().remove(closest);
        img.clear(Globals.DEFAULT_BACKGROUND_COLOR);
        checkPolygonSizeAndDraw();
        System.out.println("Removed Point located at [" + closest.getX() + ";" + closest.getY() + "]");
    }

    private void checkPolygonSizeAndDraw() {
        if (polygon.getPoints().size() > 1) {
            drawer.drawPolygon(img, liner, polygon, Globals.BLUE);
        }
        if (croppingPolygon.getPoints().size() > 1) {
            drawer.drawPolygon(img, liner, croppingPolygon, Globals.CYAN);
        }
        panel.repaint();
    }

    private void drawLeadingLines(MouseEvent e, Polygon polygon) {
        // Draw lines from first and last point to create interactive feeling
        liner.drawLine(img, polygon.getPoints().get(0).getX(), polygon.getPoints().get(0).getY(), e.getX(), e.getY(), Globals.GREEN);
        liner.drawLine(img, polygon.getPoints().get(polygon.getPoints().size() - 1).getX(), polygon.getPoints().get(polygon.getPoints().size() - 1).getY(), e.getX(), e.getY(), Globals.GREEN);
        if (polygon.getPoints().size() > 2)
            // If the array size is larger than 2, paint in red the line that will be deleted
            liner.drawLine(img, polygon.getPoints().get(0).getX(), polygon.getPoints().get(0).getY(), polygon.getPoints().get(polygon.getPoints().size() - 1).getX(), polygon.getPoints().get(polygon.getPoints().size() - 1).getY(), Globals.RED);
    }

    private JTextArea initTextArea() {
        textArea = new JTextArea();
        textArea.setForeground(new Color(255, 255, 255));
        textArea.setFocusable(false);
        textArea.setEditable(false);
        textArea.setOpaque(false);
        return textArea;
    }

    private void prepareLineStart(int x, int y, int color) {
        img.setColor(x, y, color);
        panel.repaint();
        System.out.println("Selected new starting point [" + x + ";" + y + "]");
    }

    private void updateFillColor(int color) {
        fillColor = color;
        scanLineColor = color;
        Globals.usePattern = false;
    }

    private void draw() {
        img.clear(Globals.DEFAULT_BACKGROUND_COLOR);
    }

    private void start() {
        draw();
        panel.repaint();
    }

    private void updateText() {
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
                    CTRL to seed fill the polygon (you have to use naive line mode)
                    'S' key to change the color of the Seed (Flood) Fill algorithm
                    'K' key to change to Stack implementation
                    'M' key to change to animation mode
                    'F' key for Scan Line
                    'W' key for cropping polygon
                    'Q' key to fill cropped area
                4 - Rectangle drawer
                    CTRL to seed fill the rectangle
                    'E' key to draw the Ellipse
                        'E' key again to disable/enable leading lines
                C - clear canvas
                ESC - Exit
                                
                Mouse position:
                """ + "[" + mouseX + ";" + mouseY + "]" + "\n\n" + statusMessage;
        textArea.setText(text);
    }

    private void initNaiveLine() {
        img.clear(Globals.DEFAULT_BACKGROUND_COLOR);
        System.out.println("Changed mode to Naive Line drawer");
        liner = new NaiveLineDrawer();
        lineColor = Globals.PURPLE;
        flag = 1;
        successStatusLabel("Changed mode to Naive Line drawer");
    }

    private void initDashedLine() {
        img.clear(Globals.DEFAULT_BACKGROUND_COLOR);
        System.out.println("Changed mode to Dashed Line drawer");
        liner = new DashedLineDrawer();
        lineColor = Globals.CYAN;
        flag = 2;
        successStatusLabel("Changed mode to Dashed Line drawer");
    }

    private void initPolygonMode() {
        img.clear(Globals.DEFAULT_BACKGROUND_COLOR);
        drawer = new PolygonDrawer();
        flag = 3;
        polygon.getPoints().clear();
        croppingPolygon.getPoints().clear();
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
            errorStatusLabel("An error occurred, please check terminal");
            System.out.println("Invalid option, using Naive liner");
            System.out.println("Exception [" + exception + "]");
        }
    }

    private void swapEditAndDeleteMode() {
        if (flag == 3) {
            if (polygonRemovePointFlag) {
                System.out.println("Swapped to edit mode");
                successStatusLabel("Swapped to edit mode");
            } else {
                System.out.println("Swapped to delete mode");
                successStatusLabel("Swapped to delete mode");
            }
            polygonRemovePointFlag = !polygonRemovePointFlag;
        }
    }

    private void initChangeColorSequence() {
        if (flag == 3 || flag == 4) {
            try {
                System.out.println("Select different Fill color");
                System.out.println("""
                        1 - Red
                        2 - Green
                        3 - Blue
                        4 - Cyan
                        5 - Purple
                        6 - Pattern
                        7 - Own Color
                        """);
                switch (Integer.parseInt(bufferedReader.readLine())) {
                    case 1 -> updateFillColor(Globals.RED);
                    case 2 -> updateFillColor(Globals.GREEN);
                    case 3 -> updateFillColor(Globals.BLUE);
                    case 4 -> updateFillColor(Globals.CYAN);
                    case 5 -> updateFillColor(Globals.PURPLE);
                    case 6 -> Globals.usePattern = !Globals.usePattern;
                    case 7 -> {
                        System.out.println("Enter color in format 0xRRGGBB");
                        String input = bufferedReader.readLine();
                        try {
                            if (input.startsWith("0x")) {
                                input = input.substring(2);
                            }
                            int color = Integer.parseInt(input, 16);
                            updateFillColor(color);
                        } catch (Exception exception) {
                            fillColor = Globals.CYAN;
                            System.out.println("Exception occurred. Setting color back to default (Cyan)!");
                            System.out.println("Stack trace [" + exception + "]");
                        }
                    }
                }
            } catch (Exception exception) {
                fillColor = Globals.CYAN;
                errorStatusLabel("Unable to change color, check terminal");
                System.out.println("Exception occurred. Setting color back to default (Cyan)!");
                System.out.println("Stack trace [" + exception + "]");
            }
        }
    }

    private void swapOwnStackOrJava() {
        if (flag == 3) {
            ownStack = !ownStack;
            if (ownStack) {
                System.out.println("Switched to LIFO implementation");
                successStatusLabel("Switched to LIFO implementation");
                seedFill = new SeedFill4Stack();
            } else {
                System.out.println("Switched to Java stack");
                successStatusLabel("Switched to Java stack");
                seedFill = new SeedFill4();
            }
        }
    }

    private void switchToAnimationFill() {
        if (flag == 3) {
            System.out.println("Switched to Animation fill");
            successStatusLabel("Switched to Animation fill");
            seedFill = new SeedFill4Animation(panel);
        }
    }

    private void scanLineFill() {
        if (flag == 3) {
            successStatusLabel("Filling via Scan line");
            scanLine.fill(img, polygon, scanLineColor, drawer, liner);
            panel.repaint();
        }
    }

    private void changeActivePolygon() {
        if (flag == 3) {
            croppingPolygon.getPoints().clear();
            polygonSwitch = !polygonSwitch;
            img.clear(Globals.DEFAULT_BACKGROUND_COLOR);
            if (polygon.getPoints().size() > 1) {
                drawer.drawPolygon(img, liner, polygon, Globals.BLUE);
            }
            if (polygonSwitch) {
                System.out.println("You are now drawing polygon");
                successStatusLabel("You are now drawing polygon");
            } else {
                System.out.println("You are now drawing cropping polygon");
                successStatusLabel("You are now drawing cropping polygon");
            }
        }
    }

    private void fillCroppedArea() {
        if (flag == 3) {
            if (polygon.getPoints().size() >= 3 && croppingPolygon.getPoints().size() >= 3) {
                PolygonCutter polygonCutter = new PolygonCutter(croppingPolygon);
                try {
                    Polygon cut = polygonCutter.cut(polygon);
                    scanLine.fill(img, cut, Globals.RED, drawer, liner);
                } catch (Exception exception) {
                    System.out.println("Unable to use this cropping polygon, please create a new one using 'N' key");
                    errorStatusLabel("Unable to use this cropping polygon");
                }
            } else {
                System.out.println("Not able to use these polygons!");
            }
        }
    }

    private void createNewCropper() {
        if (flag == 3) {
            croppingPolygon.getPoints().clear();
            img.clear(Globals.DEFAULT_BACKGROUND_COLOR);
            if (polygon.getPoints().size() > 1) {
                drawer.drawPolygon(img, liner, polygon, Globals.BLUE);
            }
            System.out.println("Clearing cropping polygon");
            successStatusLabel("Clearing cropping polygon");
        }
    }

    private void initRectangleMode() {
        img.clear(Globals.DEFAULT_BACKGROUND_COLOR);
        drawer = new RectangleDrawer();
        flag = 4;
        rectangle.getPoints().clear();
        System.out.println("Changed to Rectangle drawer");
        successStatusLabel("Changed to Rectangle drawer");
    }

    private void drawEllipseInsideRectangle() {
        if (flag == 4 && rectangle.getPoints().size() == 2) {
            System.out.println("Drawing/Hiding an Ellipse");
            successStatusLabel("Drawing/Hiding an Ellipse");
            img.clear(Globals.DEFAULT_BACKGROUND_COLOR);
            drawer = new RectangleDrawer();
            drawer.drawPolygon(img, liner, rectangle, Globals.PURPLE);
            drawer = new EllipseDrawer();
            drawer.drawPolygon(img, liner, rectangle, Globals.PURPLE);
            Globals.ellipseLeads = !Globals.ellipseLeads;
            panel.repaint();
        }
    }

    private void clearCanvas() {
        System.out.println("Clearing canvas...");
        successStatusLabel("Clearing canvas...");
        img.clear(Globals.DEFAULT_BACKGROUND_COLOR);
        flag = 0;
        polygon.getPoints().clear();
        croppingPolygon.getPoints().clear();
        polygonSwitch = true;
        polygonRemovePointFlag = true;
        closest = null;
        seedFill = new SeedFill4();
        fillColor = Globals.CYAN;
        rectangle.getPoints().clear();
        statusMessage = "Welcome!";
        updateText();
        statusLabel.setText(statusMessage);
        Globals.setDefaultDashAndSpace();
        panel.repaint();
        System.out.println("Done");
        successStatusLabel("Done");
    }

    private void exitApplication() {
        System.out.println("Exiting application");
        System.exit(0);
    }

    private JPanel initGui() {
        JPanel guiPanel = new JPanel();
        guiPanel.setVisible(true);

        // Initialize components
        JButton naiveButton = new JButton("Naive Line");
        JButton dashedButton = new JButton("Dashed Line");
        JButton polygonButton = new JButton("Polygon");
        JButton rectangleButton = new JButton("Rectangle");
        JLabel selectorsLabel = new JLabel("Mode Selectors");
        JLabel widthLabel = new JLabel("Space:");
        spaceInput = new JTextField(1);
        lengthInput = new JTextField(1);
        JLabel lengthLabel = new JLabel("Length:");
        JButton spaceLengthSaveButton = new JButton("Save New Values");
        JLabel valuesLabel = new JLabel("Set New values for dashed line:");
        JLabel editDeleteLabel = new JLabel("Polygon Controls");
        JButton switchPolygonModeButton = new JButton("Switch Edit and Delete modes");
        JButton seedFillChangeButton = new JButton("Change Color of Seed Fill/Scan Line");
        JButton stackImplementationButton = new JButton("Stack Implementation of Seed FIll");
        JButton seedFillAnimationButton = new JButton("Animation mode of Seed FIll");
        JButton scanLineButton = new JButton("Scan Line");
        JButton croppingPolygonButton = new JButton("Swap active polygon");
        JButton fillCroppedAreaButton = new JButton("Fill the Cropped area");
        JButton createNewCropperButton = new JButton("Create new Cropping Polygon");
        JLabel rectangleControlLabel = new JLabel("Rectangle Controls");
        JButton drawEllipseButton = new JButton("Draw an Ellipse");
        JLabel generalLabel = new JLabel("General");
        JButton clearCanvasButton = new JButton("Clear Canvas");
        JButton exitButton = new JButton("Exit");
        JLabel statusHeaderLabel = new JLabel("Status:");
        statusLabel = new JLabel("Welcome!");

        // Set panel size
        guiPanel.setPreferredSize(new Dimension(375, 900));
        guiPanel.setLayout(null);

        // Add button Event handlers
        naiveButton.addActionListener(e -> handleButtons(1));
        dashedButton.addActionListener(e -> handleButtons(2));
        polygonButton.addActionListener(e -> handleButtons(3));
        rectangleButton.addActionListener(e -> handleButtons(4));
        spaceLengthSaveButton.addActionListener(e -> handleButtons(5));
        switchPolygonModeButton.addActionListener(e -> handleButtons(6));
        seedFillChangeButton.addActionListener(e -> handleButtons(7));
        stackImplementationButton.addActionListener(e -> handleButtons(8));
        seedFillAnimationButton.addActionListener(e -> handleButtons(9));
        scanLineButton.addActionListener(e -> handleButtons(10));
        croppingPolygonButton.addActionListener(e -> handleButtons(11));
        fillCroppedAreaButton.addActionListener(e -> handleButtons(12));
        createNewCropperButton.addActionListener(e -> handleButtons(13));
        drawEllipseButton.addActionListener(e -> handleButtons(14));
        clearCanvasButton.addActionListener(e -> handleButtons(15));
        exitButton.addActionListener(e -> handleButtons(16));

        // Add components to panel
        guiPanel.add(naiveButton);
        guiPanel.add(dashedButton);
        guiPanel.add(polygonButton);
        guiPanel.add(rectangleButton);
        guiPanel.add(selectorsLabel);
        guiPanel.add(widthLabel);
        guiPanel.add(spaceInput);
        guiPanel.add(lengthInput);
        guiPanel.add(lengthLabel);
        guiPanel.add(spaceLengthSaveButton);
        guiPanel.add(valuesLabel);
        guiPanel.add(editDeleteLabel);
        guiPanel.add(switchPolygonModeButton);
        guiPanel.add(seedFillChangeButton);
        guiPanel.add(stackImplementationButton);
        guiPanel.add(seedFillAnimationButton);
        guiPanel.add(scanLineButton);
        guiPanel.add(croppingPolygonButton);
        guiPanel.add(fillCroppedAreaButton);
        guiPanel.add(createNewCropperButton);
        guiPanel.add(rectangleControlLabel);
        guiPanel.add(drawEllipseButton);
        guiPanel.add(generalLabel);
        guiPanel.add(clearCanvasButton);
        guiPanel.add(exitButton);
        guiPanel.add(statusHeaderLabel);
        guiPanel.add(statusLabel);

        // Set absolute bounds
        naiveButton.setBounds(30, 65, 150, 35);
        dashedButton.setBounds(200, 65, 150, 35);
        polygonButton.setBounds(30, 115, 150, 35);
        rectangleButton.setBounds(200, 115, 150, 35);
        selectorsLabel.setBounds(30, 35, 100, 25);
        widthLabel.setBounds(30, 205, 45, 25);
        spaceInput.setBounds(75, 205, 90, 25);
        lengthInput.setBounds(255, 205, 90, 25);
        lengthLabel.setBounds(205, 205, 100, 25);
        spaceLengthSaveButton.setBounds(30, 235, 315, 30);
        valuesLabel.setBounds(30, 175, 185, 25);
        editDeleteLabel.setBounds(30, 295, 165, 25);
        switchPolygonModeButton.setBounds(30, 325, 315, 30);
        seedFillChangeButton.setBounds(30, 360, 315, 30);
        stackImplementationButton.setBounds(30, 395, 315, 30);
        seedFillAnimationButton.setBounds(30, 430, 315, 30);
        scanLineButton.setBounds(30, 465, 315, 30);
        croppingPolygonButton.setBounds(30, 500, 315, 30);
        fillCroppedAreaButton.setBounds(30, 535, 315, 30);
        createNewCropperButton.setBounds(30, 570, 315, 30);
        rectangleControlLabel.setBounds(30, 625, 115, 25);
        drawEllipseButton.setBounds(30, 655, 315, 30);
        generalLabel.setBounds(30, 710, 100, 25);
        clearCanvasButton.setBounds(30, 740, 315, 30);
        exitButton.setBounds(30, 775, 315, 30);
        statusHeaderLabel.setBounds(30, 820, 100, 25);
        statusLabel.setBounds(30, 850, 315, 30);

        return guiPanel;
    }

    private void successStatusLabel(String message) {
        this.statusLabel.setForeground(Color.GREEN);
        this.statusLabel.setText(message);
        this.statusMessage = message;
        updateText();
    }

    private void errorStatusLabel(String message) {
        this.statusLabel.setForeground(Color.RED);
        this.statusLabel.setText(message);
        this.statusMessage = message;
        updateText();
    }

    private void handleButtons(int buttonIndex) {

        switch (buttonIndex) {
            case 1 -> initNaiveLine();
            case 2 -> initDashedLine();
            case 3 -> initPolygonMode();
            case 4 -> initRectangleMode();
            case 5 -> {
                try {
                    Globals.spaceLength = Integer.parseInt(spaceInput.getText());
                    Globals.dashLength = Integer.parseInt(lengthInput.getText());
                    successStatusLabel("Values changed successfully");
                } catch (Exception e) {
                    Globals.setDefaultDashAndSpace();
                    errorStatusLabel("Exception occurred, please check terminal");
                    System.out.println("Exception occurred. Setting values to default!");
                    System.out.println("Stack trace [" + e + "]");
                }
            }
            case 6 -> swapEditAndDeleteMode();
            case 7 -> initChangeColorSequence();
            case 8 -> swapOwnStackOrJava();
            case 9 -> switchToAnimationFill();
            case 10 -> scanLineFill();
            case 11 -> changeActivePolygon();
            case 12 -> fillCroppedArea();
            case 13 -> createNewCropper();
            case 14 -> drawEllipseInsideRectangle();
            case 15 -> clearCanvas();
            case 16 -> {
                successStatusLabel("Exiting application");
                exitApplication();
            }
        }
        panel.requestFocusInWindow();
        panel.repaint();
    }

}