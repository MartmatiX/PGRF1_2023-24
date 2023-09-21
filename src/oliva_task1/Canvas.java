package oliva_task1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * trida pro kresleni na platno: zobrazeni pixelu
 *
 * @author PGRF FIM UHK
 * @version 2020
 */

// TODO: 21.09.2023 zacit s krizkem u prostred, posouvat ho na vsechny strany a nechavat za sebou trail aby bylo videt kudy se slo. Trasu vest od prostredku krizku

public class Canvas {

    private JFrame frame;
    private JPanel panel;
    private BufferedImage img;

    int x = 400;
    int y = 300;

    int cross_x = 200;
    int cross_y = 200;

    public Canvas(int width, int height) {
        frame = new JFrame();

        frame.setLayout(new BorderLayout());
        frame.setTitle("UHK FIM PGRF : " + this.getClass().getName());
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        panel = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                present(g);
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
                    case KeyEvent.VK_UP -> {
                        y -= 1;
                        img.setRGB(x, y, 0xffff00);
                        panel.repaint();
                    }
                    case KeyEvent.VK_DOWN -> {
                        y += 1;
                        img.setRGB(x, y, 0xffff00);
                        panel.repaint();
                    }
                    case KeyEvent.VK_LEFT -> {
                        x -= 1;
                        img.setRGB(x, y, 0xffff00);
                        panel.repaint();
                    }
                    case KeyEvent.VK_RIGHT -> {
                        x += 1;
                        img.setRGB(x, y, 0xffff00);
                        panel.repaint();
                    }
                    case KeyEvent.VK_W -> {
                        cross_y -= 1;
                        clear();
                        img.setRGB(cross_x, cross_y, 0xffff00);
                        img.setRGB(cross_x + 1, cross_y, 0xffff00);
                        img.setRGB(cross_x - 1, cross_y, 0xffff00);
                        img.setRGB(cross_x, cross_y + 1, 0xffff00);
                        img.setRGB(cross_x, cross_y - 1, 0xffff00);
                        panel.repaint();
                    }
                    case KeyEvent.VK_S -> {
                        cross_y += 1;
                        clear();
                        img.setRGB(cross_x, cross_y, 0xffff00);
                        img.setRGB(cross_x + 1, cross_y, 0xffff00);
                        img.setRGB(cross_x - 1, cross_y, 0xffff00);
                        img.setRGB(cross_x, cross_y + 1, 0xffff00);
                        img.setRGB(cross_x, cross_y - 1, 0xffff00);
                        panel.repaint();
                    }
                    case KeyEvent.VK_D -> {
                        cross_x += 1;
                        clear();
                        img.setRGB(cross_x, cross_y, 0xffff00);
                        img.setRGB(cross_x + 1, cross_y, 0xffff00);
                        img.setRGB(cross_x - 1, cross_y, 0xffff00);
                        img.setRGB(cross_x, cross_y + 1, 0xffff00);
                        img.setRGB(cross_x, cross_y - 1, 0xffff00);
                        panel.repaint();
                    }
                    case KeyEvent.VK_A -> {
                        cross_x -= 1;
                        clear();
                        img.setRGB(cross_x, cross_y, 0xffff00);
                        img.setRGB(cross_x + 1, cross_y, 0xffff00);
                        img.setRGB(cross_x - 1, cross_y, 0xffff00);
                        img.setRGB(cross_x, cross_y + 1, 0xffff00);
                        img.setRGB(cross_x, cross_y - 1, 0xffff00);
                        panel.repaint();
                    }
                }
            }
        });
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                img.setRGB(e.getX(), e.getY(), 0xff0000);
                panel.repaint();
            }
        });
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                img.setRGB(e.getX(), e.getY(), 0x00ff00);
                panel.repaint();

            }
        });
        panel.requestFocus();
    }

    public void clear() {
        Graphics gr = img.getGraphics();
        gr.setColor(new Color(0x2f2f2f));
        gr.fillRect(0, 0, img.getWidth(), img.getHeight());
    }

    public void present(Graphics graphics) {
        graphics.drawImage(img, 0, 0, null);
    }

    public void draw() {
        clear();
        img.setRGB(400, 300, 0xffff00);
    }

    public void start() {
        draw();
        panel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Canvas(800, 600).start());
    }

}