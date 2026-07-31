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

  // Ejercicios
  getExercises(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/ejercicios`, { headers: this.getHeaders() });
  }

  createExercise(exercise: { nombre: string; descripcion: string; anguloObjetivo: number }): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/ejercicios`, exercise, { headers: this.getHeaders() });
  }

  // Pacientes
  getPatients(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/pacientes`, { headers: this.getHeaders() });
  }

  createPatient(patient: { nombre: string; correo: string; password: string }): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/pacientes`, patient, { headers: this.getHeaders() });
  }

  // Asignaciones de Ejercicios
  getAssignedExercises(patientId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/asignaciones/paciente/${patientId}`, { headers: this.getHeaders() });
  }

  assignExercise(data: { pacienteId: number; ejercicioId: number; indicaciones: string }): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/asignaciones`, data, { headers: this.getHeaders() });
  }

  deleteAssignment(id: number): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/asignaciones/${id}`, { headers: this.getHeaders() });
  }

  // Sesiones de terapia
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
