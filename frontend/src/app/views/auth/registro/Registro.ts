import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../auth/auth.service';
import { User } from '../../../models/User';

@Component({
  selector: 'app-cadastro',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './registro.html',
})
export class Registro implements OnInit {
  cadastroForm!: FormGroup;
  isLoading = false;

  selectedRole: 'ESTUDANTE' | 'MENTOR' = 'ESTUDANTE';

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.cadastroForm = this.fb.group({
      nome: ['', [Validators.required]],
      email: [
        '',
        [
          Validators.required,
          Validators.email,
          // Permite subdomínios como @discente.ufape.edu.br ou apenas @ufape.edu.br
          Validators.pattern(/^[a-zA-Z0-9._%+-]+@([a-zA-Z0-9.-]+\.)?ufape\.edu\.br$/i),
        ],
      ],
      senha: ['', [Validators.required, Validators.minLength(8)]],
      role: ['ESTUDANTE', [Validators.required]],
      termos: [false, [Validators.requiredTrue]],
    });
  }

  selectRole(role: 'ESTUDANTE' | 'MENTOR') {
    this.selectedRole = role;
    this.cadastroForm.patchValue({ role: role });
  }

  irLogin() {
    this.router.navigate(['/login']);
  }
  irTermos() {
    this.router.navigate(['/termos']);
  }

  onSubmit() {
    if (this.cadastroForm.invalid) return;

    this.isLoading = true;
    const { nome, email, senha, role } = this.cadastroForm.value;

    const novoUsuario: User = { name: nome, email, password: senha, role };

    this.authService.register(novoUsuario).subscribe({
      next: () => {
        this.authService.login(email, senha).subscribe({
          next: (sucesso) => {
            this.isLoading = false;
            if (sucesso) {
              if (role === 'MENTOR') {
                this.router.navigate(['/mentor/cadastro']);
              } else {
                this.router.navigate(['/dashboard']);
              }
            }
          },
          error: () => {
            this.isLoading = false;
            this.router.navigate(['/login']);
          },
        });
      },
      error: (err) => {
        this.isLoading = false;
        alert(err.error?.message || 'Erro no cadastro');
      },
    });
  }
}
