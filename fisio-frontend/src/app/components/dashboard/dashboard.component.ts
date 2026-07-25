import { Component, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ApiService } from '../../services/api.service';
import { Chart } from 'chart.js/auto';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit, AfterViewInit {
  currentUser: any;
  isDoctor = false;

  // Business Data
  sessions: any[] = [];
  exercises: any[] = [];
  
  // Patients list (for Doctor monitoring)
  patientsList: any[] = [
    { id: 1, nombre: 'Juan Perez', correo: 'paciente@fisioterapia.com' }
  ];
  selectedPatientId = 1;
  selectedPatientName = 'Juan Perez';

  // New Exercise Form (Doctor only)
  newExerciseName = '';
  newExerciseDescription = '';
  newExerciseTargetAngle = 35;

  // Stats Counters
  totalSessions = 0;
  totalReps = 0;
  totalErrors = 0;

  @ViewChild('chartCanvas') chartCanvas!: ElementRef;
  chart: any;

  constructor(
    private authService: AuthService,
    private apiService: ApiService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }

    this.currentUser = this.authService.getCurrentUser();
    this.isDoctor = this.currentUser.rol === 'MEDICO';

    this.loadExercises();
    
    if (this.isDoctor) {
      this.loadPatientHistory(this.selectedPatientId);
    } else {
      this.loadPatientHistory(this.currentUser.id);
    }
  }

  ngAfterViewInit(): void {
    // Initial build is triggered once data loads from API
  }

  loadExercises(): void {
    this.apiService.getExercises().subscribe({
      next: (data) => {
        this.exercises = data;
      },
      error: (err) => console.error('Error loading exercises', err)
    });
  }

  onPatientChange(): void {
    const selected = this.patientsList.find(p => p.id === Number(this.selectedPatientId));
    if (selected) {
      this.selectedPatientName = selected.nombre;
      this.loadPatientHistory(selected.id);
    }
  }

  loadPatientHistory(patientId: number): void {
    this.apiService.getPatientHistory(patientId).subscribe({
      next: (data) => {
        this.sessions = data;
        this.calculateStats();
        setTimeout(() => this.buildChart(), 150); // Small delay to let DOM render canvas
      },
      error: (err) => console.error('Error loading patient history', err)
    });
  }

  calculateStats(): void {
    this.totalSessions = this.sessions.length;
    this.totalReps = this.sessions.reduce((acc, s) => acc + s.repeticionesExitosas, 0);
    this.totalErrors = this.sessions.reduce((acc, s) => acc + s.erroresCometidos, 0);
  }

  buildChart(): void {
    if (this.chart) {
      this.chart.destroy();
    }

    if (!this.chartCanvas) return;

    const sortedSessions = [...this.sessions].reverse();
    const labels = sortedSessions.map(s => new Date(s.fecha).toLocaleDateString());
    const repsData = sortedSessions.map(s => s.repeticionesExitosas);
    const errorsData = sortedSessions.map(s => s.erroresCometidos);

    this.chart = new Chart(this.chartCanvas.nativeElement, {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [
          {
            label: 'Repeticiones Exitosas',
            data: repsData,
            backgroundColor: '#3182ce',
            borderColor: '#3182ce',
            borderWidth: 1,
            borderRadius: 4
          },
          {
            label: 'Errores Cometidos',
            data: errorsData,
            backgroundColor: '#e53e3e',
            borderColor: '#e53e3e',
            borderWidth: 1,
            borderRadius: 4
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'top',
            labels: {
              boxWidth: 12,
              font: {
                size: 12
              }
            }
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              stepSize: 1
            }
          }
        }
      }
    });
  }

  onCreateExercise(): void {
    if (!this.newExerciseName || !this.newExerciseDescription) return;

    const payload = {
      nombre: this.newExerciseName,
      descripcion: this.newExerciseDescription,
      anguloObjetivo: this.newExerciseTargetAngle
    };

    this.apiService.createExercise(payload).subscribe({
      next: (data) => {
        this.exercises.push(data);
        this.newExerciseName = '';
        this.newExerciseDescription = '';
        this.newExerciseTargetAngle = 35;
      },
      error: (err) => console.error('Error creating exercise', err)
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
