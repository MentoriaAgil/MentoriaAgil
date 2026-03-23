import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../../auth/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.html',
})
export class Login {
  loginForm: FormGroup;
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]],
    });
  }

  onSubmit() {
    if (this.loginForm.valid) {
      const { email, password } = this.loginForm.value;

      this.authService.login(email, password).subscribe({
        next: (sucesso) => {
          if (sucesso) {
            // Verifica a role do usuário logado para decidir a rota
            if (this.authService.hasRole('MENTOR')) {
              this.router.navigate(['/dashboard']);
            } else {
              this.router.navigate(['/mentores']); // Estudantes vão para a lista de mentores
            }
          } else {
            this.errorMessage = 'Falha no login. Verifique e-mail/senha.';
          }
        },
        error: () => {
          this.errorMessage = 'Ocorreu um erro ao tentar fazer login.';
        },
      });
    }
  }
}
