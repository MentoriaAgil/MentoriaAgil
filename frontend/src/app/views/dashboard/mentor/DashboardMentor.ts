import { Component, OnInit, inject, signal } from '@angular/core';
import { MentoriaService } from '../../../services/mentoria/mentoria.service';
import { SolicitacaoMentoriaResponse } from '../../../models/SolicitacaoMentoriaResponse';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { DisponibilidadeService } from '../../../services/disponibilidade/disponibilidade-mentor.service';

@Component({
  selector: 'app-dashboard-mentor',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
    MatSnackBarModule,
  ],
  templateUrl: './dashboard_mentor.html',
})
export class DashboardMentor implements OnInit {
  private mentoriaService = inject(MentoriaService);
  private disponibilidadeService = inject(DisponibilidadeService);
  private snackBar = inject(MatSnackBar);

  solicitacoesPendentes = signal<SolicitacaoMentoriaResponse[]>([]);
  sessoesAgendadas = signal<any[]>([]);

  novaData = signal<string>('');
  horaInicio = signal<string>('');
  horaFim = signal<string>('');
  minhasDisponibilidades: any;

  ngOnInit() {
    this.carregarDados();
  }

  carregarDados() {
    this.carregarSolicitacoes();
    this.carregarSessoes();
    this.carregarDisponibilidades();
  }

  carregarSolicitacoes() {
    this.mentoriaService.getSolicitacoesRecebidas().subscribe({
      next: (res) => {
        this.solicitacoesPendentes.set(res.filter((s) => s.status === 'PENDING'));
      },
      error: () => this.snackBar.open('Erro ao carregar solicitações', 'Fechar'),
    });
  }

  carregarSessoes() {
    this.mentoriaService.getSessoesMentor().subscribe({
      next: (res) => this.sessoesAgendadas.set(res),
      error: () => this.snackBar.open('Erro ao carregar sessões agendadas', 'Fechar'),
    });
  }

  carregarDisponibilidades() {
    // Busca as disponibilidades do mentor logado
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    this.disponibilidadeService.listarPorMentor(user.id).subscribe((res) => {
      this.minhasDisponibilidades.set(res);
    });
  }

  salvarDisponibilidade() {
    const dto = {
      dataHoraInicio: `${this.novaData()}T${this.horaInicio()}:00`,
      dataHoraFim: `${this.novaData()}T${this.horaFim()}:00`,
    };

    this.disponibilidadeService.cadastrar(dto).subscribe({
      next: () => {
        this.snackBar.open('Disponibilidade salva!', 'OK');
        this.carregarDisponibilidades();
      },
      error: (err) => this.snackBar.open(err.error.message || 'Erro ao salvar', 'Fechar'),
    });
  }

  decidir(id: number, aceitar: boolean) {
    let justificativa = '';
    if (!aceitar) {
      const resp = prompt('Motivo da recusa:');
      if (resp === null) return;
      justificativa = resp;
    }

    const status = aceitar ? 'ACCEPTED' : 'REJECTED';
    this.mentoriaService.atualizarStatus(id, status, justificativa).subscribe({
      next: () => {
        this.snackBar.open(aceitar ? 'Aceite!' : 'Recusada.', 'Fechar', { duration: 3000 });
        this.carregarSolicitacoes();
      },
    });
  }
}
