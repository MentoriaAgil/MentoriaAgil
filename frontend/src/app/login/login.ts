import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../auth'; 

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule], 
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginComponent {
  loginForm: FormGroup; 
  errorMessage: string | null = null; 

  constructor(
    private fb: FormBuilder,
    private authService: AuthService 
  ) {
   
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]]
    });
  }

  onSubmit() {
    if (this.loginForm.valid) {
      console.log("Enviando para o Java...");
      
      this.authService.login(this.loginForm.value).subscribe({
        next: (resposta) => {
          
          console.log('Sucesso:', resposta);
          alert('LOGIN REALIZADO COM SUCESSO! 🚀');
        },
        error: (erro) => {
          
          console.error('Erro:', erro);
          this.errorMessage = "Falha no login. Verifique e-mail/senha ";
        }
      });
      
    } else {
      this.errorMessage = "Preencha todos os campos.";
    }
  }
}