import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DrawingPanel extends JPanel {
    Stroke firstStroke;
    Stroke lastStroke;
    Stroke currentStroke;
    int brushThickness = 4;
    Color brushColor = Color.BLACK;
    int cursorX = 0;
    int cursorY = 0;
    boolean cursorVisible = false;

    UndoStack undoStack = new UndoStack();
    RedoStack redoStack = new RedoStack();

    public DrawingPanel() {
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                currentStroke = new Stroke();
                currentStroke.thickness = brushThickness;
                currentStroke.color = brushColor;
                redoStack.clear();

                if (firstStroke == null) {
                    firstStroke = currentStroke;
                    lastStroke = currentStroke;
                } else {
                    lastStroke.next = currentStroke;
                    currentStroke.prev = lastStroke;
                    lastStroke = currentStroke;
                }

                currentStroke.addPoint(e.getX(), e.getY());
                repaint();
            }

            public void mouseReleased(MouseEvent e) {
                if (currentStroke != null) {
                    System.out.println("Stroke finished");
                    undoStack.push(currentStroke);
                    System.out.println("Stroke added to undo stack");

                    currentStroke = null;
                    repaint();
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (currentStroke != null) {
                    currentStroke.addPoint(e.getX(), e.getY());
                    repaint();
                }
            }
        });
    }

    public void undo() {
        System.out.println("Undo method called");

        if (undoStack.isEmpty()) {
            System.out.println("Undo stack is empty");
            return;
        }
        Stroke stroke = undoStack.pop();

        if (stroke.prev != null) {
            stroke.prev.next = stroke.next;
        } else {
            firstStroke = stroke.next;
        }

        if (stroke.next != null) {
            stroke.next.prev = stroke.prev;
        } else {
            lastStroke = stroke.prev;
        }

        stroke.prev = null;
        stroke.next = null;

        redoStack.push(stroke);
        repaint();
    }

    public void redo() {
        System.out.println("Redo method called");

        if (redoStack.isEmpty()) {
            System.out.println("Redo stack is empty");
            return;
        }

        Stroke stroke = redoStack.pop();

        stroke.prev = lastStroke;
        stroke.next = null;

        if (lastStroke == null) {
            firstStroke = stroke;
        } else {
            lastStroke.next = stroke;
        }

        lastStroke = stroke;
        undoStack.push(stroke);

        repaint();
    }

    public void clearCanvas() {
        firstStroke = null;
        lastStroke = null;
        currentStroke = null;

        undoStack.clear();
        redoStack.clear();

        repaint();
    }

    public void processGesture(String gesture, int x, int y) {
        int canvasX = x * Math.max(0, getWidth() - 1) / 1000;
        int canvasY = y * Math.max(0, getHeight() - 1) / 1000;
        cursorX = canvasX;
        cursorY = canvasY;
        cursorVisible = true;
        repaint();

        if (gesture.equals("DRAW")) {
            if (currentStroke == null) {
                currentStroke = new Stroke();
                currentStroke.thickness = brushThickness;
                currentStroke.color = brushColor;
                redoStack.clear();

                if (firstStroke == null) {
                    firstStroke = currentStroke;
                    lastStroke = currentStroke;
                } else {
                    lastStroke.next = currentStroke;
                    currentStroke.prev = lastStroke;
                    lastStroke = currentStroke;
                }
            }

            currentStroke.addPoint(canvasX, canvasY);
            repaint();
        } else {

            if (currentStroke != null) {
                undoStack.push(currentStroke);
                currentStroke = null;
                repaint();
            }

            if (gesture.equals("UNDO")) {
                undo();
            } else if (gesture.equals("REDO")) {
                redo();
            } else if (gesture.equals("CLEAR")) {
                clearCanvas();
            } else if (gesture.equals("THICKER")) {
                brushThickness = Math.min(20, brushThickness + 2);
            } else if (gesture.equals("THINNER")) {
                brushThickness = Math.max(1, brushThickness - 2);
            } else if (gesture.equals("COLOR_RED")) {
                brushColor = Color.RED;
            } else if (gesture.equals("COLOR_BLUE")) {
                brushColor = Color.BLUE;
            } else if (gesture.equals("COLOR_GREEN")) {
                brushColor = Color.GREEN;
            } else if (gesture.equals("COLOR_BLACK")) {
                brushColor = Color.BLACK;
            }
        }

    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Stroke stroke = firstStroke;

        while (stroke != null) {
            PointNode point = stroke.head;
            g.setColor(stroke.color);
            while (point != null && point.next != null) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;

                g2.setStroke(new java.awt.BasicStroke(stroke.thickness));
                g2.drawLine(
                        point.x, point.y,
                        point.next.x, point.next.y);

                point = point.next;
            }

            if (stroke.head != null && stroke.head.next == null) {
                g.fillOval(stroke.head.x, stroke.head.y, 3, 3);
            }
            stroke = stroke.next;
        }
        if (cursorVisible) {
            if (currentStroke != null) {
                g.setColor(Color.RED);
            } else {
                g.setColor(Color.GRAY);
            }

            g.drawOval(cursorX - 7, cursorY - 7, 14, 14);
            g.fillOval(cursorX - 2, cursorY - 2, 4, 4);
        }
    }
}
