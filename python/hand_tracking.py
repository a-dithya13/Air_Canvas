
import cv2
import mediapipe as mp
import socket
import time

mp_hands = mp.solutions.hands
mp_draw = mp.solutions.drawing_utils

hands = mp_hands.Hands(
    static_image_mode=False,
    max_num_hands=1,
    min_detection_confidence=0.7,
    min_tracking_confidence=0.7
)

cap = cv2.VideoCapture(0)
client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
client.connect(("localhost", 5000))

print("Connected to Java!")


def classify_gesture(hand):
    thumb_tip = hand.landmark[4]
    thumb_ip = hand.landmark[3]

    index_tip = hand.landmark[8]
    index_pip = hand.landmark[6]

    middle_tip = hand.landmark[12]
    middle_pip = hand.landmark[10]

    ring_tip = hand.landmark[16]
    ring_pip = hand.landmark[14]

    pinky_tip = hand.landmark[20]
    pinky_pip = hand.landmark[18]

    index_up = index_tip.y < index_pip.y
    middle_up = middle_tip.y < middle_pip.y
    ring_up = ring_tip.y < ring_pip.y
    pinky_up = pinky_tip.y < pinky_pip.y

    thumb_up = thumb_tip.y < thumb_ip.y
    thumb_down = thumb_tip.y > thumb_ip.y

    if index_up and pinky_up and not middle_up and not ring_up:
        return "THINNER"

    if pinky_up and not index_up and not middle_up and not ring_up:
        return "THICKER"

    if index_up and middle_up and ring_up and not pinky_up:
        return "COLOR_RED"
    if index_up and middle_up and ring_up and pinky_up and thumb_up:
        return "CLEAR"

    if index_up and middle_up and not ring_up and not pinky_up:
        return "MOVE"

    if index_up and not middle_up and not ring_up and not pinky_up:
        return "DRAW"

    if not index_up and not middle_up and not ring_up and not pinky_up:
        if thumb_down:
            return "UNDO"
        if thumb_up:
            return "REDO"

    return "NONE"



if not cap.isOpened():
    print("Could not open webcam")
    hands.close()
    exit()

while True:
    success, frame = cap.read()

    if not success:
        print("Could not read webcam frame")
        break

    frame = cv2.flip(frame, 1)
    rgb_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
    results = hands.process(rgb_frame)

    gesture = "NONE"
    x = 500
    y = 500

    if results.multi_hand_landmarks:
        for hand_landmarks in results.multi_hand_landmarks:
            mp_draw.draw_landmarks(
                frame,
                hand_landmarks,
                mp_hands.HAND_CONNECTIONS
            )

            gesture = classify_gesture(hand_landmarks)
        landmark = hand_landmarks.landmark[8]

        x = int(landmark.x * 1000)
        y = int(landmark.y * 1000)

        x  = max(0, min(1000, x))
        y = max(0, min(1000, y))    
  
    cv2.rectangle(
        frame, (0, 0), (frame.shape[1], 75),
        (18, 18, 18), -1
    )

    cv2.rectangle(
        frame, (18, 18), (22, 55),
        (0, 0, 255), -1
    )

    cv2.putText(
        frame,
        "GESTURE // " + gesture,
        (35, 48),
        cv2.FONT_HERSHEY_SIMPLEX,
        0.8,
        (240, 240, 240),
        2,
        cv2.LINE_AA
    )

    message = f"{gesture},{x},{y}\n"
    client.sendall(message.encode())
    cv2.imshow("Air Canvas - Gesture Recognition", frame)

    if cv2.waitKey(1) & 0xFF == ord("q"):
        break

cap.release()
hands.close()
client.close()
cv2.destroyAllWindows()