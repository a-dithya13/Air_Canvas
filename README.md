# Air Canvas

Air Canvas is a gesture-controlled drawing application that lets users draw on a digital canvas using hand movements captured by a webcam. It combines Python-based hand tracking with a Java Swing interface and custom data structures for managing strokes and drawing history.

## Features

- Real-time hand tracking through a webcam using OpenCV and MediaPipe.
- Index-finger drawing and a separate cursor-movement gesture.
- Undo, redo, and clear operations.
- Adjustable brush thickness and colour selection.
- Mouse-based drawing as an alternative input method.
- Java Swing desktop interface.
- Local TCP socket communication between the Python and Java programs.

## Technology Stack

- **Python:** webcam processing and gesture classification.
- **OpenCV:** webcam capture and image display.
- **MediaPipe:** hand landmark detection.
- **Java:** canvas, event handling, stroke management, and data structures.
- **Java Swing/AWT:** desktop GUI and drawing.
- **TCP sockets:** communication between the Python client and Java server.

## Project Structure

```text
Air_Canvas/
├── java/
│   ├── Main.java
│   ├── DrawingPanel.java
│   ├── GestureServer.java
│   ├── PointNode.java
│   ├── Stroke.java
│   ├── UndoStack.java
│   └── RedoStack.java
├── python/
│   ├── hand_tracking.py
│   └── requirements.txt
├── .gitignore
├── start_air_canvas.bat
└── README.md
```

The filenames above describe the intended repository layout. If the local requirements file still has the misspelling `requirments.txt`, rename it to `requirements.txt` and update any scripts that refer to it.

## Requirements

- Windows (the supplied launcher is a Windows batch file).
- A supported Java JDK with `java` and `javac` available on `PATH`.
- Python compatible with the pinned packages in `python/requirements.txt`.
- A working webcam.
- Internet access for initial dependency installation, if dependencies are not already installed.

## Setup

### 1. Clone the repository

```bash
git clone https://github.com/a-dithya13/Air_Canvas.git
cd Air_Canvas
```

### 2. Create a Python virtual environment

From the repository root, run:

```bat
py -3.12 -m venv python\.venv
python\.venv\Scripts\python.exe -m pip install --upgrade pip
python\.venv\Scripts\python.exe -m pip install -r python\requirements.txt
```

If Python 3.12 is not installed, use a Python version supported by the package versions in `requirements.txt`.

### 3. Start the application

From the repository root, run:

```bat
start_air_canvas.bat
```

The launcher compiles the Java source and starts the Java application before launching Python. The Java socket server listens on `localhost` port `5000`, and the Python program connects to it.

If the webcam does not open, close other applications using it and check Windows camera permissions.

## How It Works

1. OpenCV captures frames from the webcam.
2. MediaPipe detects hand landmarks in each frame.
3. Python classifies the current hand pose and determines the index-fingertip position.
4. Python scales the position to the range `0–1000` and sends a newline-terminated message to Java.
5. The Java server parses the message and passes the gesture and coordinates to the drawing panel.
6. The drawing panel updates the current stroke, canvas history, or drawing settings.
7. Swing repaints the canvas to display the result.

### Communication format

The Python client sends one text message per frame in this format:

```text
GESTURE,x,y
```

For example:

```text
DRAW,412,230
```

The gesture name identifies the detected action. The coordinates are normalized to the range `0–1000`. Messages are newline-terminated and transmitted over a local TCP connection.

## Gesture Controls

The current gesture classifier is heuristic and should be tested under different hand orientations and lighting conditions.

| Gesture | Action |
|---|---|
| Index finger raised | Draw |
| Index and middle fingers raised | Move cursor without drawing |
| Fist with thumb down | Undo |
| Thumbs up | Redo |
| Open palm | Clear canvas |
| Pinky raised | Increase brush thickness |
| Index finger and pinky raised | Decrease brush thickness |
| Three fingers raised | Switch to red |
| Other poses or no detected hand | No action |

The exact classifier rules are implemented in `python/hand_tracking.py`. Gesture recognition depends on the camera view, hand orientation, and the current heuristic thresholds.

## Data Structures

The project uses custom structures to manage drawing data and history. The original project specification also calls for a circular gesture-confirmation buffer and a circular-array event queue. Check the current source code before claiming that every required structure has been completed.

| Data structure | Intended role |
|---|---|
| Doubly linked list of strokes | Maintains the order of visible strokes and allows removal/restoration of a stroke. |
| Singly linked list of points with a tail pointer | Stores the points in each stroke and appends a new point efficiently. |
| Bounded circular-array undo stack | Keeps a limited history of undoable strokes. The specification limits this to the latest three completed strokes. |
| Linked-node redo stack | Stores strokes that have been undone and may be restored. |
| Circular gesture-confirmation buffer | Intended to stabilize gesture recognition by considering recent detections. |
| Circular-array event queue | Intended to buffer events for orderly processing. |

The circular gesture-confirmation buffer and event queue must be verified in the code before they are described as implemented features.

## Important Behaviour Rules

- A stroke continues while the `DRAW` gesture remains active.
- Any non-`DRAW` gesture ends the current stroke.
- Strokes receive sequential IDs such as `S1`, `S2`, and `S3`.
- `UNDO`, `REDO`, and `CLEAR` should execute once per continuous pose, not once per camera frame.
- Starting a new stroke clears the redo history.
- Only the latest three completed strokes should be undoable; older strokes remain visible.
- `CLEAR` removes all strokes and resets undo/redo history.

## Troubleshooting

- **Java is not found:** install a JDK and ensure `java` and `javac` are available in `PATH`.
- **Python dependencies fail to install:** check your Python version and install the packages listed in `python/requirements.txt` inside the project's virtual environment.
- **Connection refused:** start the Java application first and confirm that port `5000` is available.
- **No hand is detected:** improve lighting, keep the hand within the camera frame, and check camera permissions.
- **Gestures trigger incorrectly:** test the hand poses and adjust the heuristic classifier in `hand_tracking.py`.
- **The GUI opens but does not draw:** confirm that Python connected to the Java server and that messages use the expected format.

## Future Improvements

- Add a live visualization panel for the stroke list, undo stack, and redo stack.
- Complete and test the circular gesture-confirmation buffer and circular-array event queue if they are not yet implemented.
- Improve gesture stability with temporal confirmation and clearer feedback.
- Improve socket lifecycle handling and ensure Swing UI updates occur on the Event Dispatch Thread.
- Add automated tests for stack limits, history reset, stroke ordering, and gesture edge-triggering.

## Project Status

This project is under development. The core drawing workflow and GUI have been tested locally by the developer. The required data structures and edge cases should be checked against the project specification before marking the project fully complete.

## License

No license has been added yet. Add a license file if you intend to grant others permission to reuse, modify, and distribute the project.
