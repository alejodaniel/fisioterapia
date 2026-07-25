import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient, private authService: AuthService) {}

  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  getExercises(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/ejercicios`, { headers: this.getHeaders() });
  }

  createExercise(exercise: { nombre: string; descripcion: string; anguloObjetivo: number }): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/ejercicios`, exercise, { headers: this.getHeaders() });
  }

  getPatientHistory(patientId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/sesiones/${patientId}`, { headers: this.getHeaders() });
  }

  saveTherapySession(session: {
    pacienteId: number;
    ejercicioId: number;
    repeticionesExitosas: number;
    erroresCometidos: number;
    observaciones: string;
  }): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/sesiones`, session, { headers: this.getHeaders() });
  }
}
