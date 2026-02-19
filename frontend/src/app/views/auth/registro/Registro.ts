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

  // Controla visualmente qual card está selecionado no HTML
  selectedRole: 'ESTUDANTE' | 'MENTOR' = 'ESTUDANTE';

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.cadastroForm = this.fb.group({
      nome: ['', [Validators.required]],
      email: [
        '',
        [
          Validators.required,
          Validators.email,
          Validators.pattern(/^[a-z0-9._%+-]+@ufape\.edu\.br$/),
        ],
      ],
      senha: ['', [Validators.required, Validators.minLength(8)]],
      // Inicia com o valor padrão selecionado
      role: ['ESTUDANTE', [Validators.required]],
      termos: [false, [Validators.requiredTrue]]
    });
  }

  /**
   * Atualiza a seleção visual e o valor no formulário reativo
   */
  selectRole(role: 'ESTUDANTE' | 'MENTOR') {
    this.selectedRole = role;
    this.cadastroForm.patchValue({ role: role });
  }

  irLogin() { this.router.navigate(['/login']); }
  irTermos() { this.router.navigate(['/termos']); }

  onSubmit() {
    if (this.cadastroForm.invalid) {
      this.cadastroForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    const formValues = this.cadastroForm.value;

    const novoUsuario: User = {
      name: formValues.nome,
      email: formValues.email,
      password: formValues.senha,
      role: formValues.role // Envia 'ESTUDANTE' ou 'MENTOR'
    };
    this.authService.register(novoUsuario).subscribe({
      next: () => {
        this.isLoading = false;

        // Lógica de navegação condicional
        if (this.selectedRole === 'MENTOR') {
          // Se for mentor, vai para a segunda etapa do cadastro
          this.router.navigate(['/mentor/cadastro']);
        } else {
          // Se for estudante, vai direto para o dashboard ou login
          alert('Cadastro realizado com sucesso!');
          this.router.navigate(['/login']);
        }
      },
      error: (err: any) => {
        this.isLoading = false;
        const mensagemErro = err.error?.message || 'Falha ao realizar o cadastro.';
        alert(`Erro: ${mensagemErro}`);
      }
    })
  }
}
