import java.awt.Color;
public class Stroke {
    private static int nextId = 1;

    String id;
    PointNode head;
    PointNode tail;
    Stroke prev;
    Stroke next;
    int thickness = 4;
    Color color = Color.BLACK;

    public Stroke() {
        id = "S" + nextId++;

        head = null;
        tail = null;
        prev = null;
        next = null;
    }

    public void addPoint(int x, int y) {
        PointNode point = new PointNode(x, y);
        if (head == null) {
            head = point;
            tail = point;
        } else {
            tail.next = point;
            tail = point;
        }
    }
}
