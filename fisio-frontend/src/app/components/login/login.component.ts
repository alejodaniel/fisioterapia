import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  isLoginMode = true;

  // Form Fields
  nombre = '';
  correo = '';
  password = '';
  rol = 'PACIENTE';

  errorMessage = '';
  successMessage = '';

  constructor(private authService: AuthService, private router: Router) {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/dashboard']);
    }
  }

  toggleMode(): void {
    this.isLoginMode = !this.isLoginMode;
    this.errorMessage = '';
    this.successMessage = '';
    this.nombre = '';
    this.correo = '';
    this.password = '';
    this.rol = 'PACIENTE';
  }

  onSubmit(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.isLoginMode) {
      this.authService.login({ correo: this.correo, password: this.password }).subscribe({
        next: () => {
          this.router.navigate(['/dashboard']);
        },
        error: (err) => {
          this.errorMessage = err.error?.error || 'Invalid credentials. Please try again.';
        }
      });
    } else {
      const payload = {
        nombre: this.nombre,
        correo: this.correo,
        password: this.password,
        rol: this.rol
      };
      this.authService.register(payload).subscribe({
        next: () => {
          this.successMessage = 'Registration successful! Logging in...';
          setTimeout(() => {
            this.authService.login({ correo: this.correo, password: this.password }).subscribe({
              next: () => this.router.navigate(['/dashboard']),
              error: () => {
                this.isLoginMode = true;
                this.successMessage = '';
              }
            });
          }, 1500);
        },
        error: (err) => {
          this.errorMessage = err.error?.error || 'Registration failed. Email might already be in use.';
        }
      });
    }
  }
}
