import { Component, inject, OnInit, signal } from '@angular/core';
import { PerfilMentorService } from '../../services/perfil-mentor.service';
import { PerfilMentor } from '../../models/PerfilMentor';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { SolicitacaoMentoriaModalComponent } from '../mentoria/solicitacao-mentoria-modal.component';

@Component({
  selector: 'app-mentor-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './mentor-list.component.html',
  styleUrls: ['./mentor-list.component.css'],
})
export class MentorListComponent implements OnInit {
  private readonly mentorService = inject(PerfilMentorService);
  private readonly dialog = inject(MatDialog);

  // Signal garante que a lista apareça imediatamente ao carregar
  mentores = signal<PerfilMentor[]>([]);

  filtros = {
    areaPrincipal: '',
    tipoMentoria: '',
    disponibilidade: '',
    ordenacao: 'name',
  };

  ngOnInit(): void {
    this.buscar();
  }

  buscar() {
    this.mentorService.buscarMentores(this.filtros).subscribe({
      next: (data) => {
        // Filtra ativos e aplica ordenação alfabética padrão
        let lista = (data || []).filter((m) => m.ativo !== false);

        if (this.filtros.ordenacao === 'name') {
          lista.sort((a, b) => (a.name || '').localeCompare(b.name || ''));
        }

        this.mentores.set(lista);
      },
      error: (err) => console.error('Erro ao carregar mentores', err),
    });
  }

  onFiltroChange() {
    this.buscar();
  }

  solicitarMentoria(mentor: PerfilMentor): void {
    this.dialog.open(SolicitacaoMentoriaModalComponent, {
      width: '500px',
      data: { mentor },
    });
  }
}
