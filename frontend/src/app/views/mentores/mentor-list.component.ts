import { Component, OnInit } from '@angular/core';
import { PerfilMentorService } from '../../services/perfil-mentor.service';
import { PerfilMentor } from '../../models/PerfilMentor';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-mentor-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './mentor-list.component.html',
  styleUrls: ['./mentor-list.component.css']
})
export class MentorListComponent implements OnInit {

  mentores: PerfilMentor[] = [];

  filtros = {
    areaPrincipal: '',
    tipoMentoria: '',
    disponibilidade: '',
    ordenacao: ''
  };

  constructor(private mentorService: PerfilMentorService) {}

  ngOnInit(): void {
    this.buscar();
  }

  buscar() {
    this.mentorService.buscarMentores(this.filtros)
      .subscribe(data => {

        let lista = data.filter(m => m.ativo);

        if (this.filtros.ordenacao === 'nome') {
          lista = lista.sort((a, b) => a.name.localeCompare(b.name));
        }

        this.mentores = lista;
      });
  }

  onFiltroChange() {
    this.buscar();
  }
}