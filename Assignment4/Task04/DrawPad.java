package Task04;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Task 4 - Event-Handling Forensics
 * Complete the Code: DrawPad.java
 * Student: Moksha Dude (192372374)
 */
public class DrawPad extends Frame {
    private final List<Point> points = new ArrayList<>();
    private final Label status = new Label("Clicks: 0");

    public DrawPad() {
        super("Draw Pad");
        add(status, BorderLayout.SOUTH);
        addMouseListener(new MouseAdapter() {
            // TODO 1: Override mouseClicked correctly: store the point, update the
            //   label and request a repaint
            @Override
            public void mouseClicked(MouseEvent e) {
                points.add(new Point(e.getX(), e.getY()));
                status.setText("Clicks: " + points.size());
                repaint();
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) { dispose(); }
        });
        setSize(400, 300);
    }

    @Override
    public void paint(Graphics g) {
        // TODO 2: Draw every stored point as a 6 × 6 filled circle centred on the point
        super.paint(g);
        for (Point p : points) {
            g.fillOval(p.x - 3, p.y - 3, 6, 6);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Task 4: DrawPad Event Forensics ===");
        System.out.println("1. Defect Identified: Method was originally named `mouseclicked` (lowercase 'c').");
        System.out.println("   Because MouseAdapter provides an empty default `mouseClicked(MouseEvent)`, misspelling it");
        System.out.println("   merely defined an unrelated helper method. Mouse clicks were never intercepted!");
        System.out.println("2. Repaint Persistence: Drawing with `getGraphics()` writes directly to the temporary video frame.");
        System.out.println("   Upon minimization, OS repainting clears the buffer. Storing coordinates in `List<Point>`");
        System.out.println("   and redrawing them inside `paint(Graphics g)` guarantees persistent rendering.");
        System.out.println("3. Compile-time Safety: Adding `@Override` causes the compiler to flag naming typos instantly.");

        new DrawPad().setVisible(true);
    }
}
