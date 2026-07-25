import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private socket: WebSocket | null = null;
  private messageSubject = new Subject<any>();

  constructor() {}

  connect(url: string = 'ws://localhost:5000'): Observable<any> {
    this.socket = new WebSocket(url);

    this.socket.onopen = () => {
      console.log('Connected to Python computer vision WebSocket server');
    };

    this.socket.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data);
        this.messageSubject.next(data);
      } catch (e) {
        console.error('Error parsing WebSocket message:', e);
      }
    };

    this.socket.onclose = () => {
      console.log('Disconnected from computer vision WebSocket server');
    };

    this.socket.onerror = (error) => {
      console.error('WebSocket error:', error);
    };

    return this.messageSubject.asObservable();
  }

  sendFrame(base64Image: string, exerciseName: string = ''): void {
    if (this.socket && this.socket.readyState === WebSocket.OPEN) {
      // Remove metadata prefix "data:image/jpeg;base64," if present
      const base64Data = base64Image.split(',')[1] || base64Image;
      this.socket.send(JSON.stringify({ 
        frame: base64Data, 
        exercise: exerciseName 
      }));
    }
  }

  disconnect(): void {
    if (this.socket) {
      this.socket.close();
      this.socket = null;
    }
  }
}
