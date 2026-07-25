import asyncio
import base64
import json
import math
import cv2
import numpy as np
import mediapipe as mp
import websockets
from mediapipe.python.solutions import pose as mp_pose
from mediapipe.python.solutions import hands as mp_hands
from mediapipe.python.solutions import drawing_utils as mp_drawing

# Calculate distance between 2D points
def calculate_distance(p1, p2):
    return math.sqrt((p1[0] - p2[0])**2 + (p1[1] - p2[1])**2)

async def analyze_pose(websocket):
    print("New connection established from Angular frontend (AI Mode).")
    
    # Initialize MediaPipe processors locally for this connection session
    pose = mp_pose.Pose(min_detection_confidence=0.5, min_tracking_confidence=0.5)
    hands = mp_hands.Hands(max_num_hands=1, min_detection_confidence=0.5, min_tracking_confidence=0.5)

    # Session State Variables
    reps = 0
    errors = 0
    feedback = "Realice el movimiento frente a su webcam."
    has_error = False
    was_error_active = False

    # State Machine Variables
    # Neck: "NEUTRAL" or "TILTED"
    neck_state = "NEUTRAL"
    # Wrist: "FLAT" or "FLEXED"
    wrist_state = "FLAT"
    # Fingers: "CLOSED" or "OPEN"
    fingers_state = "CLOSED"

    try:
        async for message in websocket:
            # 1. Parse JSON payload
            data = json.loads(message)
            frame_data = data.get("frame", "")
            exercise_name = data.get("exercise", "")

            if not frame_data:
                continue

            # 2. Decode base64 image
            img_bytes = base64.b64decode(frame_data)
            np_arr = np.frombuffer(img_bytes, np.uint8)
            frame = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)

            if frame is None:
                continue

            h, w, _ = frame.shape
            has_error = False

            # 3. Branch analysis based on the selected exercise
            if "Cuello" in exercise_name:
                # --- CERVICAL NECK TILT EXERCISE ---
                rgb_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
                results = pose.process(rgb_frame)

                if results.pose_landmarks:
                    landmarks = results.pose_landmarks.landmark
                    
                    # Keypoints (normalized 0-1): Nose (0), Left Eye (2), Right Eye (5)
                    nose = [landmarks[mp_pose.PoseLandmark.NOSE.value].x * w, landmarks[mp_pose.PoseLandmark.NOSE.value].y * h]
                    l_eye = [landmarks[mp_pose.PoseLandmark.LEFT_EYE.value].x * w, landmarks[mp_pose.PoseLandmark.LEFT_EYE.value].y * h]
                    r_eye = [landmarks[mp_pose.PoseLandmark.RIGHT_EYE.value].x * w, landmarks[mp_pose.PoseLandmark.RIGHT_EYE.value].y * h]
                    
                    # Try to get shoulders for torso tilting error (if visible)
                    l_shoulder = landmarks[mp_pose.PoseLandmark.LEFT_SHOULDER.value]
                    r_shoulder = landmarks[mp_pose.PoseLandmark.RIGHT_SHOULDER.value]

                    # Calculate head tilt angle using eyes (extremely stable close up)
                    eye_dx = l_eye[0] - r_eye[0]
                    eye_dy = l_eye[1] - r_eye[1]
                    eye_angle = math.degrees(math.atan2(eye_dy, eye_dx))

                    # Normalize angle to range [-90, 90]
                    if eye_angle > 90:
                        eye_angle -= 180
                    elif eye_angle < -90:
                        eye_angle += 180
                    
                    abs_tilt = abs(eye_angle)

                    # Torso leaning error detection (if shoulders are visible)
                    if l_shoulder.visibility > 0.5 and r_shoulder.visibility > 0.5:
                        ls_pt = [l_shoulder.x * w, l_shoulder.y * h]
                        rs_pt = [r_shoulder.x * w, r_shoulder.y * h]
                        sh_dx = ls_pt[0] - rs_pt[0]
                        sh_dy = ls_pt[1] - rs_pt[1]
                        sh_angle = math.degrees(math.atan2(sh_dy, sh_dx))
                        if sh_angle > 90:
                            sh_angle -= 180
                        elif sh_angle < -90:
                            sh_angle += 180

                        # Cheating: leaning whole body past 8 degrees
                        if abs(sh_angle) > 8.0:
                            has_error = True
                            feedback = "¡Alerta! Mantenga los hombros alineados. No incline su torso."

                    # Neck movement state machine
                    if abs_tilt < 8.0:
                        if neck_state == "TILTED":
                            reps += 1
                            feedback = f"¡Excelente! Repetición completada ({reps})."
                        neck_state = "NEUTRAL"
                        if not has_error:
                            feedback = "Incline la cabeza lentamente hacia un hombro."
                    elif abs_tilt > 18.0:
                        neck_state = "TILTED"
                        if not has_error:
                            feedback = f"¡Buen rango cervical ({int(abs_tilt)}°)! Regrese al centro."
                    else:
                        if not has_error:
                            feedback = f"Inclinando... Ángulo: {int(abs_tilt)}° (Meta: 35°)"
                else:
                    feedback = "No se detecta el rostro. Por favor colóquese frente a la cámara."

            elif "Muñeca" in exercise_name:
                # --- WRIST FLEXION EXERCISE ---
                rgb_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
                results = hands.process(rgb_frame)

                if results.multi_hand_landmarks:
                    hand_landmarks = results.multi_hand_landmarks[0].landmark
                    
                    # Keypoints: Wrist (0), Middle Finger MCP (9), Middle Finger Tip (12)
                    wrist = [hand_landmarks[0].x * w, hand_landmarks[0].y * h]
                    mcp = [hand_landmarks[9].x * w, hand_landmarks[9].y * h]
                    tip_12 = [hand_landmarks[12].x * w, hand_landmarks[12].y * h]

                    hand_scale = calculate_distance(wrist, mcp)

                    if hand_scale > 0:
                        hand_vector = [mcp[0] - wrist[0], mcp[1] - wrist[1]]
                        
                        # Angle relative to horizontal (assuming forearm is horizontal)
                        angle_rad = math.atan2(-hand_vector[1], hand_vector[0])
                        angle = math.degrees(angle_rad)
                        if angle < 0:
                            angle += 360

                        deviation = abs(90 - (angle % 180))
                        bending_angle = 90 - deviation

                        # Posture error check: Curling fingers instead of keeping palm straight (using scale-safe threshold)
                        middle_finger_len = calculate_distance(mcp, tip_12)
                        finger_ratio = middle_finger_len / hand_scale

                        if finger_ratio < 0.38:
                            has_error = True
                            feedback = "¡Alerta! Mantenga los dedos estirados al flexionar la muñeca."

                        # Wrist movement state machine
                        if bending_angle < 15.0:
                            if wrist_state == "FLEXED":
                                reps += 1
                                feedback = f"¡Perfecto! Flexión de muñeca completada ({reps})."
                            wrist_state = "FLAT"
                            if not has_error:
                                feedback = "Doble la muñeca hacia arriba manteniendo los dedos rectos."
                        elif bending_angle > 35.0:
                            wrist_state = "FLEXED"
                            if not has_error:
                                feedback = f"Excelente flexión ({int(bending_angle)}°). Mantenga y baje."
                        else:
                            if not has_error:
                                feedback = f"Flexionando... Ángulo: {int(bending_angle)}° (Meta: 60°)"
                    else:
                        feedback = "Alinee la mano frente a la cámara."
                else:
                    feedback = "Coloque su mano y antebrazo frente a la cámara."

            elif "Dedos" in exercise_name:
                # --- FINGER SPREADING EXERCISE ---
                rgb_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
                results = hands.process(rgb_frame)

                if results.multi_hand_landmarks:
                    hand_landmarks = results.multi_hand_landmarks[0].landmark
                    
                    wrist = [hand_landmarks[0].x, hand_landmarks[0].y]
                    mcp = [hand_landmarks[9].x, hand_landmarks[9].y]
                    hand_scale = calculate_distance(wrist, mcp)

                    if hand_scale > 0:
                        # Posture error check: Hand turned sideways (index to pinky MCP distance too thin)
                        mcp_index = [hand_landmarks[5].x, hand_landmarks[5].y]
                        mcp_pinky = [hand_landmarks[17].x, hand_landmarks[17].y]
                        palm_width = calculate_distance(mcp_index, mcp_pinky) / hand_scale

                        if palm_width < 0.26:
                            has_error = True
                            feedback = "¡Alerta! Coloque la palma de la mano plana frente a la cámara."

                        # Sum distances from wrist to fingertips
                        fingertips = [4, 8, 12, 16, 20]
                        distance_sum = 0
                        for tip in fingertips:
                            tip_pt = [hand_landmarks[tip].x, hand_landmarks[tip].y]
                            distance_sum += calculate_distance(wrist, tip_pt) / hand_scale

                        # Finger spread state machine
                        if distance_sum < 5.6:
                            if fingers_state == "OPEN":
                                reps += 1
                                feedback = f"¡Excelente! Apertura de dedos completada ({reps})."
                            fingers_state = "CLOSED"
                            if not has_error:
                                feedback = "Abra la palma extendiendo los dedos al máximo."
                        elif distance_sum > 7.1:
                            fingers_state = "OPEN"
                            if not has_error:
                                feedback = "¡Mano bien abierta! Ahora cierre el puño."
                        else:
                            if not has_error:
                                feedback = "Abra o cierre la mano por completo."
                    else:
                        feedback = "Alinee la mano frente a la cámara."
                else:
                    feedback = "Coloque su mano con la palma visible frente a la cámara."

            else:
                feedback = "Ejercicio no reconocido por el sistema."

            # Update Error Count on state transition to active error
            if has_error:
                if not was_error_active:
                    errors += 1
                    was_error_active = True
            else:
                was_error_active = False

            # 4. Compile and send the real-time AI response payload
            response = {
                "reps": reps,
                "errors": errors,
                "feedback": feedback,
                "has_error": has_error
            }
            await websocket.send(json.dumps(response))

    except websockets.exceptions.ConnectionClosed as e:
        print(f"Connection closed by client: {e}")
    except Exception as e:
        print(f"Error occurred in AI execution: {e}")
    finally:
        # Clean up resources
        pose.close()
        hands.close()
        print("Session finished. Client disconnected.\n")

async def main():
    print("Starting REAL-TIME AI computer vision WebSocket server on ws://localhost:5000 ...")
    async with websockets.serve(analyze_pose, "localhost", 5000):
        await asyncio.Future()  # Keep server running forever

if __name__ == "__main__":
    asyncio.run(main())
