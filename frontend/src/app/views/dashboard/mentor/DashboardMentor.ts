import { Component, OnInit, inject, signal } from '@angular/core';
import { MentoriaService } from '../../../services/mentoria/mentoria.service';
import { SolicitacaoMentoriaResponse } from '../../../models/SolicitacaoMentoriaResponse';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-dashboard-mentor',
  standalone: true,
  imports: [
    CommonModule, 
    MatCardModule, 
    MatButtonModule, 
    MatIconModule, 
    MatDividerModule,
    MatSnackBarModule
  ],
  templateUrl: './dashboard_mentor.html'
})
export class DashboardMentor implements OnInit {
  private mentoriaService = inject(MentoriaService);
  private snackBar = inject(MatSnackBar);

  // Signal para garantir que a lista apareça assim que os dados chegarem
  solicitacoesPendentes = signal<SolicitacaoMentoriaResponse[]>([]);

  ngOnInit() { 
    this.carregarSolicitacoes(); 
  }

  carregarSolicitacoes() {
    this.mentoriaService.getSolicitacoesRecebidas().subscribe({
      next: (res) => {
        // Atualiza o signal com os dados filtrados
        this.solicitacoesPendentes.set(res.filter(s => s.status === 'PENDING'));
      },
      error: () => this.snackBar.open('Erro ao carregar solicitações', 'Fechar')
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
      }
    });
  }
}