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
      console.log('Enviando para o Java...');

      this.authService.login(email, password).subscribe({
        next: (sucesso) => {
          if (sucesso) {
            console.log('Login realizado com sucesso!');
            // 4. Redireciona para a dashboard
            this.router.navigate(['/dashboard']);
          } else {
            this.errorMessage = 'Falha no login. Verifique e-mail/senha.';
          }
        },
        error: (erro) => {
          console.error('Erro:', erro);
          this.errorMessage = 'Ocorreu um erro ao tentar fazer login.';
        },
      });
    } else {
      this.errorMessage = 'Preencha todos os campos.';
    }
  }
}
