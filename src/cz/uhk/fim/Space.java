package cz.uhk.fim;

import cz.uhk.fim.raster_data.RasterBufferedImage;
import cz.uhk.fim.raster_op.NaiveLineDrawer;
import cz.uhk.fim.render.WireRenderer;
import cz.uhk.fim.solids.*;
import cz.uhk.fim.transforms.*;
import cz.uhk.fim.utilities.Globals;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Space {

    private final JPanel panel;
    private final RasterBufferedImage raster;
    private final WireRenderer wireRenderer;
    private Mat4 model = new Mat4Identity();

    private int flag = 1;
    private boolean spinFlag = false;
    private Point2D mousePos;

    private Mat4 projection = new Mat4OrthoRH(20, 20, 0.1, 200);

    private final List<Object3D> object3DS = new ArrayList<>();

    private final AxisRGB axisRGB = new AxisRGB();
    private final Cube c1 = new Cube();
    private final Prism p1 = new Prism();
    private final Octahedron o1 = new Octahedron();
    private final HexagonalPrism hg1 = new HexagonalPrism();
    private final Sphere s1 = new Sphere();
    private final Object3D ferguson = new CubicTranslation(Cubic.FERGUSON, Globals.RED);
    private final Object3D coons = new CubicTranslation(Cubic.COONS, Globals.BLUE);
    private final Object3D bezier = new CubicTranslation(Cubic.BEZIER, Globals.GREEN);

    private final Object3D movingPrism = new Prism();
    private double gammaRotation = 1;

    private int currentSolidIndex = 1;

    Camera camera = new Camera(new Vec3D(-39, -1, 5), 0, 0, 1, true);
    private final double CAMERA_SPEED = 1;

    private JLabel statusMessageLabel;

    public Space(int width, int height) {
        object3DS.addAll(List.of(axisRGB, c1, p1, o1, hg1, s1, ferguson, coons, bezier, movingPrism));

        positionSolids();

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

        frame.add(panel, BorderLayout.CENTER);
        frame.add(initGui(), BorderLayout.EAST);
        frame.pack();
        frame.setVisible(true);

        panel.requestFocus();
        panel.requestFocusInWindow();

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W -> camera = camera.up(CAMERA_SPEED);
                    case KeyEvent.VK_S -> camera = camera.down(CAMERA_SPEED);
                    case KeyEvent.VK_A -> camera = camera.left(CAMERA_SPEED);
                    case KeyEvent.VK_D -> camera = camera.right(CAMERA_SPEED);
                    case KeyEvent.VK_Q -> camera = camera.backward(CAMERA_SPEED);
                    case KeyEvent.VK_E -> camera = camera.forward(CAMERA_SPEED);
                    case KeyEvent.VK_ESCAPE -> exit();
                    case KeyEvent.VK_1 -> changeCurrentFlag(1);
                    case KeyEvent.VK_2 -> changeCurrentFlag(2);
                    case KeyEvent.VK_H -> switchMoveOrSpin();
                    case KeyEvent.VK_R -> changeCurrentSolid(1);
                    case KeyEvent.VK_T -> changeCurrentSolid(2);
                    case KeyEvent.VK_Y -> changeCurrentSolid(3);
                    case KeyEvent.VK_U -> changeCurrentSolid(4);
                    case KeyEvent.VK_I -> changeCurrentSolid(5);
                    case KeyEvent.VK_O -> changeCurrentSolid(6);
                    case KeyEvent.VK_P -> changeCurrentSolid(7);
                    case KeyEvent.VK_L -> changeCurrentSolid(8);
                    case KeyEvent.VK_NUMPAD8 -> moveObject(1, currentSolidIndex);
                    case KeyEvent.VK_NUMPAD6 -> moveObject(2, currentSolidIndex);
                    case KeyEvent.VK_NUMPAD2 -> moveObject(3, currentSolidIndex);
                    case KeyEvent.VK_NUMPAD4 -> moveObject(4, currentSolidIndex);
                    case KeyEvent.VK_NUMPAD9 -> moveObject(9, currentSolidIndex);
                    case KeyEvent.VK_NUMPAD7 -> moveObject(7, currentSolidIndex);
                    case KeyEvent.VK_F -> {
                        setCameraView(new Mat4PerspRH(Math.PI / 4, 1, 0.01, 100));
                        updateStatusMessage("You changed to perspective camera");
                    }
                    case KeyEvent.VK_G -> {
                        setCameraView(new Mat4OrthoRH(20, 20, 0.1, 200));
                        updateStatusMessage("You changed to rectangular camera");
                    }
                }
                object3DS.add(axisRGB);
            }
        });

        final double UNZOOM_MODIFIER = 0.8;
        final double ZOOM_MODIFIER = 1.2;

        panel.addMouseWheelListener(e -> {
            Mat4 scale;
            if (e.getWheelRotation() < 0) {
                scale = new Mat4Scale(ZOOM_MODIFIER, ZOOM_MODIFIER, ZOOM_MODIFIER);
            } else {
                scale = new Mat4Scale(UNZOOM_MODIFIER, UNZOOM_MODIFIER, UNZOOM_MODIFIER);
            }
            model = model.mul(scale);
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                mousePos = new Point2D(e.getX(), e.getY());
                panel.addMouseMotionListener(new MouseAdapter() {
                    @Override
                    public void mouseDragged(MouseEvent f) {
                        super.mouseDragged(f);
                        double dx = f.getX() - mousePos.getX();
                        double dy = f.getY() - mousePos.getY();

                        switch (flag) {
                            case 1 -> {
                                camera = camera.addAzimuth(-(dx) * Math.PI / 360);
                                camera = camera.addZenith(-(dy) * Math.PI / 360);

                                mousePos = new Point2D(f.getX(), f.getY());
                            }
                            case 2 -> {
                                Mat4 rot = new Mat4RotXYZ(0, (-(dy) * 0.0002), (-(dx) * 0.0002));
                                model = model.mul(rot);
                            }
                        }
                    }
                });
            }
        });

        panel.setPreferredSize(new Dimension(width, height));
        NaiveLineDrawer naiveLineDrawer = new NaiveLineDrawer();
        wireRenderer = new WireRenderer(naiveLineDrawer, raster, projection);
    }

    public void start() {
        Runnable movePrism = () -> {
            movingPrism.setTransMat(movingPrism.getTransMat().mul(new Mat4RotXYZ(0, 0, gammaRotation)));
            render();
        };

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(() -> {
            movePrism.run();
            gammaRotation += 0.001;
        }, 0, 1000 / 60, TimeUnit.MILLISECONDS);

    }

    private void updateStatusMessage(String message) {
        this.statusMessageLabel.setForeground(Color.GREEN);
        this.statusMessageLabel.setText(message);
        System.out.println(message);
    }

    private void changeCurrentSolid(int index) {
        currentSolidIndex = index;
        Object3D object3D = object3DS.get(index);
        if (index <= 5) updateStatusMessage("You selected: " + object3D.getName());
        else {
            switch (index) {
                case 6 -> updateStatusMessage("You selected: " + object3D.getName() + " Ferguson");
                case 7 -> updateStatusMessage("You selected: " + object3D.getName() + " Bezier");
                case 8 -> updateStatusMessage("You selected: " + object3D.getName() + " Coons");
            }
        }
    }

    private void changeCurrentFlag(int index) {
        this.flag = index;
        if (index == 1) {
            updateStatusMessage("You switched to camera movement");
        } else {
            updateStatusMessage("You switched to rotation");
        }
    }

    private void switchMoveOrSpin() {
        this.spinFlag = !this.spinFlag;
        if (spinFlag) {
            updateStatusMessage("You can rotate the object");
        } else {
            updateStatusMessage("You can move the object");
        }
    }
    
    private String writeControls() {
        return """
                Movement: WASD QE
                Camera Movement: 1
                Rotation: 2
                Zoom: MouseWheel

                Solids:
                Cube: R
                Prism: T
                Octahedron: Y
                Hexagonal Prism: U
                Sphere: I
                Ferguson: O
                Bezier: P
                Coons: L
                               
                Transformations:
                Up: Numpad 8
                Down: Numpad 2
                Left: Numpad 4
                Right: Numpad 6
                Forward: Numpad 7
                Back: Numpad 9
                Switch movement and Rotation: H

                Camera:
                Perspective camera: F
                Rectangular camera: G
                """;
    }

    private void positionSolids() {
        c1.setTransMat(c1.getTransMat().mul(new Mat4Transl(0, 5, 7)));
        p1.setTransMat(p1.getTransMat().mul(new Mat4Transl(0, 0, 7)));
        o1.setTransMat(o1.getTransMat().mul(new Mat4Transl(5, -5, 7)));
        hg1.setTransMat(hg1.getTransMat().mul(new Mat4Transl(0, 5, -5)));
        s1.setTransMat(s1.getTransMat().mul(new Mat4Transl(5, -5, -5)));
        ferguson.setTransMat(ferguson.getTransMat().mul(new Mat4Transl(5, 5, 0)));
        coons.setTransMat(coons.getTransMat().mul(new Mat4Transl(5, 10, 0)));
        bezier.setTransMat(bezier.getTransMat().mul(new Mat4Transl(5, 15, 0)));
    }

    private void render() {
        clearCanvas();
        wireRenderer.setView(camera.getViewMatrix());
        wireRenderer.setProjection(projection);

        List<Object3D> objectsToRender = new ArrayList<>(object3DS);

        wireRenderer.renderSpace(objectsToRender, this.model);
        present();
    }


    private void moveObject(int direction, int selectedSolid) {
        Object3D selected = object3DS.get(selectedSolid);
        Mat4 transMat = selected.getTransMat();
        double spin = 0.5;

        if (!spinFlag) {
            switch (direction) {
                case 1 -> selected.setTransMat(transMat.mul(new Mat4Transl(0, 0, 0.2)));
                case 2 -> selected.setTransMat(transMat.mul(new Mat4Transl(0, -0.2, 0)));
                case 3 -> selected.setTransMat(transMat.mul(new Mat4Transl(0, 0, -0.2)));
                case 4 -> selected.setTransMat(transMat.mul(new Mat4Transl(0, 0.2, 0)));
                case 7 -> selected.setTransMat(transMat.mul(new Mat4Transl(0.2, 0, 0)));
                case 9 -> selected.setTransMat(transMat.mul(new Mat4Transl(-0.2, 0, 0)));
            }
        } else {
            switch (direction) {
                case 1 -> selected.setTransMat(transMat.mul(new Mat4Rot(spin, 1, 0, 0)));
                case 2 -> selected.setTransMat(transMat.mul(new Mat4Rot(spin, 0, 1, 0)));
                case 3 -> selected.setTransMat(transMat.mul(new Mat4Rot(-spin, 1, 0, 0)));
                case 4 -> selected.setTransMat(transMat.mul(new Mat4Rot(-spin, 0, 1, 0)));
                case 7 -> selected.setTransMat(transMat.mul(new Mat4Rot(spin, 0, 0, 1)));
                case 9 -> selected.setTransMat(transMat.mul(new Mat4Rot(-spin, 0, 0, 1)));
            }
        }
    }

    private void clearCanvas() {
        raster.clear(Globals.DEFAULT_BACKGROUND_COLOR);
    }

    private void present() {
        if (panel.getGraphics() != null) panel.getGraphics().drawImage(raster.getImg(), 0, 0, null);
    }

    private void setCameraView(Mat4 projection) {
        this.projection = projection;
    }

    private void exit() {
        System.out.println("Goodbye!\n");
        System.exit(0);
    }

    private JPanel initGui() {
        JPanel guiPanel = new JPanel();
        guiPanel.setVisible(true);

        JLabel movementLabel = new JLabel("Controls");

        JTextArea controlsArea = new JTextArea();
        controlsArea.setFocusable(false);
        controlsArea.setText(writeControls());
        JScrollPane controlsScrollPane = new JScrollPane(controlsArea);
        controlsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JLabel solidsLabel = new JLabel("Solids");
        JButton cubeButton = new JButton("Cube");
        JButton prismButton = new JButton("Prism");
        JButton octahedronButton = new JButton("Octahedron");
        JButton sphereButton = new JButton("Sphere");
        JButton hexagonalPrismButton = new JButton("Hexagonal Prism");
        JLabel bicubicsLabel = new JLabel("Bicubics");
        JButton fergusonButton = new JButton("Ferguson");
        JButton bezierButton = new JButton("Bezier");
        JButton coonsLabel = new JButton("Coons");
        JLabel lmbSwitchLabel = new JLabel("LMB Switch");
        JButton mouseLookLabel = new JButton("Mouse Look");
        JButton rotationButton = new JButton("Rotation");
        JLabel transformationsLabel = new JLabel("Transformations");
        JButton upButton = new JButton("Up");
        JButton downButton = new JButton("Down");
        JButton leftButton = new JButton("Left");
        JButton rightButton = new JButton("Right");
        JButton forwardButton = new JButton("Forward");
        JButton backButton = new JButton("Back");
        JButton swapMoveSpinModeButton = new JButton("Switch Move or Spin");
        JLabel cameraLabel = new JLabel("Camera");
        JButton perspectiveButton = new JButton("Perspective");
        JButton rectangularButton = new JButton("Rectangular");
        JLabel generalLabel = new JLabel("General");
        JButton exitButton = new JButton("Exit");
        JLabel statusLabel = new JLabel("Status:");
        statusMessageLabel = new JLabel("Welcome!");

        guiPanel.setPreferredSize(new Dimension(375, 900));
        guiPanel.setLayout(null);

        cubeButton.addActionListener(e -> handleButtons(1));
        prismButton.addActionListener(e -> handleButtons(2));
        octahedronButton.addActionListener(e -> handleButtons(3));
        sphereButton.addActionListener(e -> handleButtons(4));
        hexagonalPrismButton.addActionListener(e -> handleButtons(5));
        fergusonButton.addActionListener(e -> handleButtons(6));
        bezierButton.addActionListener(e -> handleButtons(7));
        coonsLabel.addActionListener(e -> handleButtons(8));
        mouseLookLabel.addActionListener(e -> handleButtons(9));
        rotationButton.addActionListener(e -> handleButtons(10));
        upButton.addActionListener(e -> handleButtons(11));
        downButton.addActionListener(e -> handleButtons(12));
        leftButton.addActionListener(e -> handleButtons(13));
        rightButton.addActionListener(e -> handleButtons(14));
        forwardButton.addActionListener(e -> handleButtons(15));
        backButton.addActionListener(e -> handleButtons(16));
        swapMoveSpinModeButton.addActionListener(e -> handleButtons(17));
        perspectiveButton.addActionListener(e -> handleButtons(18));
        rectangularButton.addActionListener(e -> handleButtons(19));
        exitButton.addActionListener(e -> handleButtons(20));

        guiPanel.add(movementLabel);
        guiPanel.add(controlsScrollPane);
        guiPanel.add(solidsLabel);
        guiPanel.add(cubeButton);
        guiPanel.add(prismButton);
        guiPanel.add(octahedronButton);
        guiPanel.add(sphereButton);
        guiPanel.add(hexagonalPrismButton);
        guiPanel.add(bicubicsLabel);
        guiPanel.add(fergusonButton);
        guiPanel.add(bezierButton);
        guiPanel.add(coonsLabel);
        guiPanel.add(lmbSwitchLabel);
        guiPanel.add(mouseLookLabel);
        guiPanel.add(rotationButton);
        guiPanel.add(transformationsLabel);
        guiPanel.add(upButton);
        guiPanel.add(downButton);
        guiPanel.add(leftButton);
        guiPanel.add(rightButton);
        guiPanel.add(forwardButton);
        guiPanel.add(backButton);
        guiPanel.add(swapMoveSpinModeButton);
        guiPanel.add(cameraLabel);
        guiPanel.add(perspectiveButton);
        guiPanel.add(rectangularButton);
        guiPanel.add(generalLabel);
        guiPanel.add(exitButton);
        guiPanel.add(statusLabel);
        guiPanel.add(statusMessageLabel);

        movementLabel.setBounds(20, 5, 100, 25);
        controlsScrollPane.setBounds(20, 30, 335, 110);
        solidsLabel.setBounds(20, 140, 100, 25);
        cubeButton.setBounds(20, 165, 150, 35);
        prismButton.setBounds(200, 165, 150, 35);
        octahedronButton.setBounds(20, 205, 150, 35);
        sphereButton.setBounds(200, 205, 150, 35);
        hexagonalPrismButton.setBounds(115, 245, 150, 35);
        bicubicsLabel.setBounds(25, 285, 100, 25);
        fergusonButton.setBounds(25, 310, 150, 35);
        bezierButton.setBounds(200, 310, 150, 35);
        coonsLabel.setBounds(115, 350, 150, 35);
        lmbSwitchLabel.setBounds(25, 390, 155, 25);
        mouseLookLabel.setBounds(25, 415, 150, 35);
        rotationButton.setBounds(200, 415, 150, 35);
        transformationsLabel.setBounds(25, 460, 100, 25);
        upButton.setBounds(120, 490, 150, 35);
        downButton.setBounds(115, 580, 150, 35);
        leftButton.setBounds(25, 535, 150, 35);
        rightButton.setBounds(200, 535, 150, 35);
        forwardButton.setBounds(25, 630, 150, 35);
        backButton.setBounds(200, 630, 150, 35);
        swapMoveSpinModeButton.setBounds(25, 675, 325, 35);
        cameraLabel.setBounds(25, 710, 100, 25);
        perspectiveButton.setBounds(25, 735, 150, 35);
        rectangularButton.setBounds(200, 735, 150, 35);
        generalLabel.setBounds(25, 770, 100, 25);
        exitButton.setBounds(25, 790, 325, 35);
        statusLabel.setBounds(25, 835, 100, 25);
        statusMessageLabel.setBounds(25, 860, 300, 25);

        return guiPanel;
    }

    private void handleButtons(int index) {
        switch (index) {
            case 1 -> changeCurrentSolid(1);
            case 2 -> changeCurrentSolid(2);
            case 3 -> changeCurrentSolid(3);
            case 4 -> changeCurrentSolid(5);
            case 5 -> changeCurrentSolid(4);
            case 6 -> changeCurrentSolid(6);
            case 7 -> changeCurrentSolid(7);
            case 8 -> changeCurrentSolid(8);
            case 9 -> changeCurrentFlag(1);
            case 10 -> changeCurrentFlag(2);
            case 11 -> moveObject(1, currentSolidIndex);
            case 12 -> moveObject(3, currentSolidIndex);
            case 13 -> moveObject(4, currentSolidIndex);
            case 14 -> moveObject(2, currentSolidIndex);
            case 15 -> moveObject(7, currentSolidIndex);
            case 16 -> moveObject(9, currentSolidIndex);
            case 17 -> switchMoveOrSpin();
            case 18 -> {
                setCameraView(new Mat4PerspRH(Math.PI / 4, 1, 0.01, 100));
                updateStatusMessage("You changed to perspective camera");
            }
            case 19 -> {
                setCameraView(new Mat4OrthoRH(20, 20, 0.1, 200));
                updateStatusMessage("You changed to rectangular camera");
            }
            case 20 -> exit();
        }
        panel.requestFocusInWindow();
    }

}
