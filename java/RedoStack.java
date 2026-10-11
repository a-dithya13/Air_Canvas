
public class RedoStack {
    private class Node {
        Stroke stroke;
        Node next;

        Node(Stroke stroke) {
            this.stroke = stroke;
            this.next = null;
        }
    }

    private Node top = null;

    public void push(Stroke stroke) {
        Node node = new Node(stroke);
        node.next = top;
        top = node;
    }

    public Stroke pop() {
        if (top == null) {
            return null;
        }

        Stroke stroke = top.stroke;
        top = top.next;

        return stroke;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public void clear() {
        top = null;
    }
}
