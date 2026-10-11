import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
public class GestureServer {
     private int port;
     private DrawingPanel canvas;
    public GestureServer(int port,DrawingPanel canvas) {
        this.port = port;
        this.canvas = canvas;
    }
public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Waiting for Python on port " + port);
            try (Socket socket = serverSocket.accept();
                 BufferedReader reader = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()))) {
                System.out.println("Python connected!");
                String message;
                String previousGesture = "NONE";
                while ((message = reader.readLine()) != null) {
                    try {
                        String[] parts = message.split(",");
                        if (parts.length != 3) {
                            continue;
                        }
                        String gesture = parts[0].trim();
                        int x = Integer.parseInt(parts[1].trim());
                        int y = Integer.parseInt(parts[2].trim());
                        if (x < 0 || x > 1000 ||
                            y < 0 || y > 1000) {
                            continue;
                        }
                        if (gesture.equals("UNDO") ||
                            gesture.equals("REDO") ||
                            gesture.equals("CLEAR") ||
                            gesture.equals("THICKER") ||
                            gesture.equals("THINNER") ||
                            gesture.equals("COLOR_RED")) {
                            if (!gesture.equals(previousGesture)) {
                                canvas.processGesture(gesture, x, y);
                            }
                        } else if (gesture.equals("DRAW") ||
                                   gesture.equals("MOVE") ||
                                   gesture.equals("NONE")) {

                            canvas.processGesture(gesture, x, y);
                        } else {
                            continue;
                        }
                        previousGesture = gesture;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid coordinates: " + message);
                    }
                }

            }

        }catch (IOException e) {
            System.out.println("Socket error: " + e.getMessage());
        }
    }
}
