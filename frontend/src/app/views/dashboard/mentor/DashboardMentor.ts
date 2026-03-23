import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { MentoriaService } from '../../../services/mentoria/mentoria.service';
import { SolicitacaoMentoriaResponse } from '../../../models/SolicitacaoMentoriaResponse';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { DisponibilidadeService } from '../../../services/disponibilidade/disponibilidade-mentor.service';
import {
  ProgressoResponse,
  ProgressoService,
} from '../../../services/progresso/progresso-mentorado.service';
import { AuthService } from '../../../auth/auth.service';

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
  private progressoService = inject(ProgressoService);
  private snackBar = inject(MatSnackBar);
  private authService = inject(AuthService);

  minhasDisponibilidades = signal<any[]>([]);
  sessoesAgendadas = signal<any[]>([]);
  historicoProgresso = signal<ProgressoResponse[]>([]);
  solicitacoesPendentes = signal<SolicitacaoMentoriaResponse[]>([]);

  novaData = signal<string>('');
  horaInicio = signal<string>('');
  horaFim = signal<string>('');

  ngOnInit() {
    this.carregarDados();
  }

  carregarDados() {
    const user = this.authService.loadUser(); // Busca dados do usuário logado no localStorage/subject

    if (this.isMentor()) {
      // Lógica de Mentor
      this.carregarSolicitacoes();
      this.carregarSessoesMentor();
    } else {
      // Lógica de Mentorado
      this.carregarSessoesMentorado();
      if (user?.id) {
        this.carregarHistorico(user.id);
      }
    }
  }

  carregarSessoesMentor() {
    this.mentoriaService.getSessoesMentor().subscribe({
      next: (res) => this.sessoesAgendadas.set(res),
      error: () => this.snackBar.open('Erro ao carregar sessões de mentor.', 'Fechar'),
    });
  }

  carregarSessoesMentorado() {
    this.mentoriaService.getSessoes().subscribe({
      next: (res) => this.sessoesAgendadas.set(res),
      error: () => this.snackBar.open('Erro ao carregar suas sessões.', 'Fechar'),
    });
  }

  carregarHistorico(mentoradoId: number) {
    this.progressoService.listarPorMentorado(mentoradoId).subscribe({
      next: (res) => this.historicoProgresso.set(res),
      error: () => console.error('Erro ao carregar histórico de evolução'),
    });
  }

  carregarDisponibilidades() {
    this.disponibilidadeService.listarMinhas().subscribe({
      next: (res) => this.minhasDisponibilidades.set(res),
      error: () => this.snackBar.open('Erro ao carregar sua agenda.', 'Fechar'),
    });
  }

  salvarDisponibilidade() {
    // Validação de preenchimento
    if (!this.novaData() || !this.horaInicio() || !this.horaFim()) {
      this.snackBar.open('Preencha a data e os horários de início e fim.', 'Aviso');
      return;
    }

    const dto = {
      dataHoraInicio: `${this.novaData()}T${this.horaInicio()}:00`,
      dataHoraFim: `${this.novaData()}T${this.horaFim()}:00`,
    };

    this.disponibilidadeService.cadastrar(dto).subscribe({
      next: () => {
        this.snackBar.open('Horário cadastrado com sucesso!', 'OK');
        this.carregarDisponibilidades();
        // Limpar formulário
        this.novaData.set('');
        this.horaInicio.set('');
        this.horaFim.set('');
      },
      error: (err) => {
        const msg = err.error?.error || 'Erro 400: Conflito de horários ou data inválida.';
        this.snackBar.open(msg, 'Fechar');
      },
    });
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
      error: () => this.snackBar.open('Erro ao carregar sessões', 'Fechar'),
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

  abrirRegistroProgresso(sessao: any) {
    const mentoradoId = sessao.mentorado?.id || sessao.mentoradoId;
    const descricao = prompt(`Registre a evolução de ${sessao.mentoradoNome}:`);

    if (descricao?.trim()) {
      this.progressoService.registrar(mentoradoId, descricao).subscribe({
        next: () => {
          this.snackBar.open('Evolução registrada!', 'OK');
          this.verHistorico(mentoradoId);
        },
        error: (err) => this.snackBar.open(err.error?.error || 'Erro ao registrar', 'Fechar'),
      });
    }
  }

  editarProgresso(progresso: ProgressoResponse) {
    const novaDescricao = prompt('Editar observação:', progresso.descricao);
    if (novaDescricao && novaDescricao !== progresso.descricao) {
      this.progressoService.atualizar(progresso.id, novaDescricao).subscribe(() => {
        this.snackBar.open('Registro atualizado!', 'OK');
        this.verHistorico(progresso.mentoradoId);
      });
    }
  }

  excluirProgresso(progresso: ProgressoResponse) {
    if (confirm('Deseja realmente excluir este registro de evolução?')) {
      this.progressoService.excluir(progresso.id).subscribe(() => {
        this.snackBar.open('Registro removido.', 'OK');
        this.verHistorico(progresso.mentoradoId);
      });
    }
  }

  registrarEvolucao(sessao: any) {
    const descricao = prompt(`Descreva a evolução de ${sessao.mentoradoNome}:`);
    if (!descricao) return;

    this.progressoService.registrar(sessao.mentoradoId, descricao).subscribe({
      next: () => {
        this.snackBar.open('Progresso registrado!', 'OK', { duration: 2000 });
        this.verHistorico(sessao.mentoradoId); // Atualiza a visão
      },
      error: (err) => this.snackBar.open(err.error.message || 'Erro ao salvar', 'Fechar'),
    });
  }

  verHistorico(mentoradoId: number) {
    this.progressoService.listarPorMentorado(mentoradoId).subscribe((res) => {
      this.historicoProgresso.set(res);
    });
  }

  isMentor = computed(() => this.authService.getUserRole() === 'MENTOR');
}
