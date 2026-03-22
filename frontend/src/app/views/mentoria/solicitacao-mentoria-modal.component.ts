import { Component, Inject, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
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
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSnackBarModule
  ],
  templateUrl: './solicitacao-mentoria-modal.component.html',
  styleUrls: ['./solicitacao-mentoria-modal.component.css']
})
export class SolicitacaoMentoriaModalComponent {
  private readonly fb: FormBuilder = inject(FormBuilder);
  private readonly mentoriaService: MentoriaService = inject(MentoriaService);
  private readonly snackBar: MatSnackBar = inject(MatSnackBar);
  private readonly dialogRef: MatDialogRef<SolicitacaoMentoriaModalComponent> = inject(MatDialogRef<SolicitacaoMentoriaModalComponent>);

  loading = false;

  form = this.fb.group({
    message: ['', [Validators.required, Validators.maxLength(500)]]
  });

  constructor(@Inject(MAT_DIALOG_DATA) public data: { mentor: PerfilMentor }) {}

  cancelar(): void {
    this.dialogRef.close(false);
  }

  enviar(): void {
    if (this.form.invalid) return;

    this.loading = true;
    const request = {
      mentorId: (this.data.mentor as any).userId || this.data.mentor.id, 
      message: this.form.value.message!
    };

    this.mentoriaService.solicitarMentoria(request).subscribe({
      next: () => {
        this.snackBar.open('Solicitação enviada com sucesso!', 'Fechar', { duration: 3000 });
        this.dialogRef.close(true);
      },
      error: (err: any) => {
        this.loading = false;
        const mensagem = err.error?.error || 'Erro ao enviar solicitação. Tente novamente.';
        this.snackBar.open(mensagem, 'Fechar', { duration: 5000 });
      }
    });
  }
}
