import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; 
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-cadastro',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule], 
  templateUrl: './cadastro.html', 
})
export class CadastroComponent implements OnInit {
  cadastroForm!: FormGroup;

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.cadastroForm = this.fb.group({
      nome: ['', [Validators.required]],
      // Validação E-mail UFAPE
      email: ['', [
        Validators.required, 
        Validators.email,
        Validators.pattern(/^[a-z0-9._%+-]+@ufape\.edu\.br$/)
      ]],
      // Validação Senha 8 caracteres
      senha: ['', [Validators.required, Validators.minLength(8)]],
      role: ['', [Validators.required]]
    });
  }

  onSubmit() {
    if (this.cadastroForm.valid) {
      console.log("Dados prontos para o banco:", this.cadastroForm.value);
    } else {
      this.cadastroForm.markAllAsTouched(); // Mostra os erros se clicar sem preencher
    }
  }
}