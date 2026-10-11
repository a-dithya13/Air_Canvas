
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.lang.Thread;
import java.awt.Color;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Air Canvas");
        JPanel header = new JPanel(new BorderLayout());
        header.add(new JLabel("AIR CANVAS"), BorderLayout.WEST);
        frame.add(header, BorderLayout.NORTH);
        DrawingPanel canvas = new DrawingPanel();
        frame.add(canvas);
        JButton undoButton = new JButton("Undo");
        JButton redoButton = new JButton("Redo");
        JButton clearButton = new JButton("Clear");
        JPanel buttons = new JPanel();
        buttons.setBackground(new Color(34, 38, 46));
        buttons.setBorder(
                BorderFactory.createEmptyBorder(12, 15, 12, 15));
        frame.add(buttons, BorderLayout.SOUTH);
        buttons.add(undoButton);
        buttons.add(redoButton);
        buttons.add(clearButton);
                
        undoButton.setBackground(new Color(65, 72, 85));
        undoButton.setForeground(Color.WHITE);
        undoButton.setFocusPainted(false);
        undoButton.setOpaque(true);
        undoButton.setBorderPainted(false);

        redoButton.setBackground(new Color(65, 72, 85));
        redoButton.setForeground(Color.WHITE);
        redoButton.setFocusPainted(false);
        redoButton.setOpaque(true);
        redoButton.setBorderPainted(false);


        clearButton.setBackground(new Color(185, 64, 80));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.setOpaque(true);
        clearButton.setBorderPainted(false);
        undoButton.addActionListener(e -> {
            System.out.println("Undo clicked");
            canvas.undo();
        });
        redoButton.addActionListener(e -> {
            System.out.println("Redo clicked");
            canvas.redo();
        });
        clearButton.addActionListener(e -> canvas.clearCanvas());

        frame.setLayout(new BorderLayout());
        frame.add(canvas, BorderLayout.CENTER);
        frame.add(buttons, BorderLayout.SOUTH);

        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        GestureServer server = new GestureServer(5000, canvas);

        Thread serverThread = new Thread(() -> server.start());
        serverThread.setDaemon(true);
        serverThread.start();
        frame.setVisible(true);
    }
}