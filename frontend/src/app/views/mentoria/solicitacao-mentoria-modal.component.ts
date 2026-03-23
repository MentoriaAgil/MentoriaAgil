import { Component, Inject, inject, signal, ChangeDetectorRef, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { PerfilMentor } from '../../models/PerfilMentor';
import { MentoriaService } from '../../services/mentoria/mentoria.service';

@Component({
  selector: 'app-solicitacao-mentoria-modal',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatSelectModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSnackBarModule,
  ],
  templateUrl: './solicitacao-mentoria-modal.component.html',
  styleUrls: ['./solicitacao-mentoria-modal.component.css'],
})
export class SolicitacaoMentoriaModalComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly mentoriaService = inject(MentoriaService);
  private readonly snackBar = inject(MatSnackBar);
  private readonly dialogRef = inject(MatDialogRef<SolicitacaoMentoriaModalComponent>);
  private readonly cdr = inject(ChangeDetectorRef);

  loading = signal(false);
  // Lista de slots combinados vindos do backend
  slotsDisponiveis = signal<{ valor: string; label: string; fim: string }[]>([]);

  form = this.fb.group({
    horarioSelecionado: ['', [Validators.required]],
    formato: ['ONLINE', [Validators.required]],
    linkReuniao: [''],
    endereco: [''],
    message: ['', [Validators.maxLength(500)]],
  });

  constructor(@Inject(MAT_DIALOG_DATA) public data: { mentor: PerfilMentor }) {}

  ngOnInit(): void {
    this.carregarHorarios();
  }

  carregarHorarios(): void {
    const mentorId = this.data.mentor.userId || this.data.mentor.id;

    this.mentoriaService.getDisponibilidadeByMentor(mentorId).subscribe({
      next: (disponibilidades) => {
        const formatados = disponibilidades.map((d) => ({
          valor: d.dataHoraInicio,
          fim: d.dataHoraFim,
          label: `${new Date(d.dataHoraInicio).toLocaleDateString('pt-BR')} das ${new Date(d.dataHoraInicio).toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' })} às ${new Date(d.dataHoraFim).toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' })}`,
        }));
        this.slotsDisponiveis.set(formatados);
      },
      error: () => this.snackBar.open('Erro ao carregar agenda do mentor.', 'Fechar'),
    });
  }

  cancelar(): void {
    this.dialogRef.close(false);
  }

  enviar(): void {
    if (this.form.invalid) return;
    this.loading.set(true);

    const slot = this.slotsDisponiveis().find(
      (s) => s.valor === this.form.value.horarioSelecionado,
    );

    const request = {
      mentorId: this.data.mentor.userId,
      dataHoraInicio: this.form.value.horarioSelecionado,
      dataHoraFim: slot?.fim,
      formato: this.form.value.formato,
      linkReuniao: this.form.value.linkReuniao || null,
      endereco: this.form.value.endereco || null,
    };

    this.mentoriaService.agendarSessao(request).subscribe({
      next: () => {
        this.snackBar.open('Sessão agendada com sucesso!', 'Fechar', { duration: 3000 });
        this.dialogRef.close(true);
      },
      error: (err) => {
        this.loading.set(false);
        this.snackBar.open(err.error?.message || 'Falha ao agendar.', 'Fechar');
      },
    });
  }
}
