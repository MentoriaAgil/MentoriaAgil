// app/views/dashboard/mentor/DisponibilidadeComponent.ts
import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../auth/auth.service';
import { PerfilMentorService } from '../perfil-mentor.service';
import { DisponibilidadeService } from './disponibilidade-mentor.service';

@Component({
  selector: 'app-disponibilidade',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    MatSnackBarModule,
  ],
  templateUrl: './disponibilidade.component.html',
})
export class DisponibilidadeComponent implements OnInit {
  private authService = inject(AuthService);
  private perfilService = inject(PerfilMentorService);
  private dispService = inject(DisponibilidadeService);
  private snackBar = inject(MatSnackBar);

  perfil = signal<any>(null);
  disponibilidades = signal<any[]>([]);

  dataSelecionada = '';
  horaInicio = '';
  horaFim = '';

  ngOnInit() {
    this.carregarDadosMentor();
  }

  carregarDadosMentor() {
    // Resgata o usuário logado para buscar o perfil e horários
    this.authService.currentUser$.subscribe((user) => {
      if (user?.id) {
        this.perfilService.getPerfilByUserId(user.id).subscribe((res) => {
          this.perfil.set(res);
        });
        this.carregarHorarios(user.id);
      }
    });
  }

  carregarHorarios(mentorId: number) {
    this.dispService.listarPorMentor(mentorId).subscribe((res) => {
      this.disponibilidades.set(res);
    });
  }

  salvarPerfil() {
    if (this.perfil()) {
      this.perfilService.updatePerfil(this.perfil().id, this.perfil()).subscribe({
        next: () => this.snackBar.open('Perfil atualizado!', 'OK', { duration: 3000 }),
        error: () => this.snackBar.open('Erro ao atualizar perfil', 'Fechar'),
      });
    }
  }

  salvarHorario() {
    if (!this.dataSelecionada || !this.horaInicio || !this.horaFim) {
      this.snackBar.open('Preencha a data e os horários', 'Fechar');
      return;
    }

    const dto = {
      dataHoraInicio: `${this.dataSelecionada}T${this.horaInicio}:00`,
      dataHoraFim: `${this.dataSelecionada}T${this.horaFim}:00`,
    };

    this.dispService.cadastrar(dto).subscribe({
      next: () => {
        this.snackBar.open('Horário adicionado!', 'OK', { duration: 3000 });
        this.carregarDadosMentor();
      },
      error: (err) => this.snackBar.open(err.error?.message || 'Erro ao salvar', 'Fechar'),
    });
  }
}
