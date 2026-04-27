import { Component } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-auth-login',
  standalone: true,
  imports: [RouterModule, FormsModule, CommonModule],
  templateUrl: './auth-login.component.html',
  styleUrl: './auth-login.component.scss'
})
export class AuthLoginComponent {
  username = '';
  password = '';
  errorMessage = '';
  isLoading = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  login() {
    this.isLoading = true;
    this.errorMessage = '';
    this.authService.login(this.username, this.password).subscribe({
      next: (user) => {
        const role = (user?.role || 'patient').toLowerCase();
        if (role === 'patient') {
          this.router.navigate(['/patient/wellbeing']); // Redirect patients to wellbeing
        } else if (role === 'caregiver' || role === 'doctor' || role === 'admin') {
          this.router.navigate(['/caregiver/monitoring']); // Redirect docs/caregivers to monitoring
        } else {
          this.router.navigate(['/homePage']);
        }
      },
      error: (err) => {
        this.errorMessage = err;
        this.isLoading = false;
      }
    });
  }

  SignInOptions = [
    {
      image: 'assets/images/authentication/google.svg',
      name: 'Google'
    },
    {
      image: 'assets/images/authentication/twitter.svg',
      name: 'Twitter'
    },
    {
      image: 'assets/images/authentication/facebook.svg',
      name: 'Facebook'
    }
  ];
}
