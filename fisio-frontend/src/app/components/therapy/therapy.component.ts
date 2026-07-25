import { Component, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ApiService } from '../../services/api.service';
import { WebSocketService } from '../../services/websocket.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-therapy',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './therapy.component.html',
  styleUrls: ['./therapy.component.css']
})
export class TherapyComponent implements OnInit, OnDestroy {
  currentUser: any;
  exercises: any[] = [];
  selectedExerciseId: number | null = null;
  selectedExerciseName = '';

  // Session Statistics
  repsCount = 0;
  errorsCount = 0;
  feedbackMessage = 'Seleccione un ejercicio e inicie la cámara para comenzar.';
  hasPostureError = false;

  // Session States
  isSessionActive = false;
  successMessage = '';

  // Camera & frame capture interval
  private stream: MediaStream | null = null;
  private captureIntervalId: any = null;
  private audioCtx: AudioContext | null = null;

  @ViewChild('videoElement') videoElement!: ElementRef<HTMLVideoElement>;
  
  private webSocketSubscription: Subscription | null = null;

  constructor(
    private authService: AuthService,
    private apiService: ApiService,
    private webSocketService: WebSocketService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }

    this.currentUser = this.authService.getCurrentUser();
    this.loadExercises();
  }

  ngOnDestroy(): void {
    this.forceCleanup();
  }

  private getAudioContext(): AudioContext {
    if (!this.audioCtx) {
      this.audioCtx = new (window.AudioContext || (window as any).webkitAudioContext)();
    }
    return this.audioCtx;
  }

  private playBeep(frequency: number, type: 'sine' | 'sawtooth', duration: number): void {
    try {
      const ctx = this.getAudioContext();
      if (ctx.state === 'suspended') {
        ctx.resume();
      }
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.type = type;
      osc.frequency.value = frequency;
      gain.gain.setValueAtTime(0.08, ctx.currentTime);
      gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + duration);
      osc.connect(gain);
      gain.connect(ctx.destination);
      osc.start();
      osc.stop(ctx.currentTime + duration);
    } catch (e) {
      console.warn('Audio Context warning:', e);
    }
  }

  private speak(text: string): void {
    if ('speechSynthesis' in window) {
      window.speechSynthesis.cancel(); // Stop current speaking queue
      const utterance = new SpeechSynthesisUtterance(text);
      utterance.lang = 'es-ES';
      utterance.rate = 1.1; // Slightly faster for responsiveness
      window.speechSynthesis.speak(utterance);
    }
  }

  loadExercises(): void {
    this.apiService.getExercises().subscribe({
      next: (data) => {
        this.exercises = data;
        if (data.length > 0) {
          this.selectedExerciseId = data[0].id;
          this.selectedExerciseName = data[0].nombre;
        }
      },
      error: (err) => console.error('Error loading exercises:', err)
    });
  }

  onExerciseChange(): void {
    const selected = this.exercises.find(e => e.id === Number(this.selectedExerciseId));
    if (selected) {
      this.selectedExerciseName = selected.nombre;
    }
  }

  startTherapy(): void {
    if (!this.selectedExerciseId) {
      this.feedbackMessage = 'Por favor, seleccione un ejercicio.';
      return;
    }

    this.repsCount = 0;
    this.errorsCount = 0;
    this.feedbackMessage = 'Iniciando cámara y conectando con el servidor de análisis...';
    this.hasPostureError = false;

    // 1. Request Webcam access
    navigator.mediaDevices.getUserMedia({ video: { width: 640, height: 480 } })
      .then(mediaStream => {
        this.stream = mediaStream;
        this.videoElement.nativeElement.srcObject = mediaStream;
        this.isSessionActive = true;
        this.feedbackMessage = 'Cámara encendida. Realice el movimiento frente a su webcam.';

        // Speak start
        this.speak("Terapia iniciada. Mire al frente.");

        // 2. Open WebSocket connection to Python vision server
        this.webSocketSubscription = this.webSocketService.connect('ws://localhost:5000').subscribe({
          next: (response) => {
            if (response) {
              const newReps = response.reps ?? this.repsCount;
              const newErrors = response.errors ?? this.errorsCount;
              const newFeedback = response.feedback ?? this.feedbackMessage;
              const newHasError = response.has_error ?? false;

              // Sound and speech alerts on counters update
              if (newReps > this.repsCount) {
                this.playBeep(880, 'sine', 0.15); // Sweet chime
                this.speak(`Repetición ${newReps}`);
              } else if (newErrors > this.errorsCount) {
                this.playBeep(220, 'sawtooth', 0.35); // Buzz warning
                this.speak(newFeedback);
              } else if (newFeedback !== this.feedbackMessage && newHasError) {
                // Speak active warning message changes
                this.speak(newFeedback);
              }

              this.repsCount = newReps;
              this.errorsCount = newErrors;
              this.feedbackMessage = newFeedback;
              this.hasPostureError = newHasError;
            }
          },
          error: (err) => {
            console.error('Error in analysis stream:', err);
            this.feedbackMessage = 'Error: No se pudo conectar con el servidor de análisis en Python (ws://localhost:5000).';
            this.speak("Error de conexión");
            this.cleanupStreams();
          }
        });

        // 3. Capture and send video frames periodically (every 150ms / ~7 FPS)
        this.captureIntervalId = setInterval(() => {
          this.captureAndSendFrame();
        }, 150);
      })
      .catch(err => {
        console.error('Webcam access error:', err);
        this.feedbackMessage = 'No se pudo acceder a la cámara. Por favor, apruebe los permisos en el navegador.';
      });
  }

  captureAndSendFrame(): void {
    if (!this.stream || !this.videoElement) return;

    const video = this.videoElement.nativeElement;
    
    // Hidden canvas for frame capturing
    const canvas = document.createElement('canvas');
    canvas.width = video.videoWidth || 640;
    canvas.height = video.videoHeight || 480;

    const ctx = canvas.getContext('2d');
    if (ctx) {
      ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
      const base64Frame = canvas.toDataURL('image/jpeg', 0.55); // 0.55 quality keeps payload light and network fast
      this.webSocketService.sendFrame(base64Frame, this.selectedExerciseName);
    }
  }

  stopAndSaveTherapy(): void {
    this.successMessage = 'Guardando resultados de sesión en el backend...';
    this.feedbackMessage = 'Sesión detenida.';

    this.cleanupStreams();

    if (this.selectedExerciseId) {
      const sessionPayload = {
        pacienteId: this.currentUser.id,
        ejercicioId: Number(this.selectedExerciseId),
        repeticionesExitosas: this.repsCount,
        erroresCometidos: this.errorsCount,
        observaciones: `Sesión de ${this.selectedExerciseName} terminada con un total de ${this.repsCount} repeticiones correctas y ${this.errorsCount} fallas de postura.`
      };

      this.apiService.saveTherapySession(sessionPayload).subscribe({
        next: () => {
          this.successMessage = '¡Sesión guardada con éxito!';
          setTimeout(() => {
            this.router.navigate(['/dashboard']);
          }, 1500);
        },
        error: (err) => {
          console.error('Error saving session:', err);
          this.successMessage = 'Error al registrar la sesión en el servidor principal de Spring Boot.';
        }
      });
    } else {
      this.router.navigate(['/dashboard']);
    }
  }

  private cleanupStreams(): void {
    if (this.captureIntervalId) {
      clearInterval(this.captureIntervalId);
      this.captureIntervalId = null;
    }

    if (this.stream) {
      this.stream.getTracks().forEach(track => track.stop());
      this.stream = null;
    }

    if (this.videoElement && this.videoElement.nativeElement) {
      this.videoElement.nativeElement.srcObject = null;
    }

    if (this.webSocketSubscription) {
      this.webSocketSubscription.unsubscribe();
      this.webSocketSubscription = null;
    }
    this.webSocketService.disconnect();
    this.isSessionActive = false;
  }

  private forceCleanup(): void {
    this.cleanupStreams();
  }
}
