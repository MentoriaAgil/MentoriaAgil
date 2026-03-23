import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class DisponibilidadeService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/disponibilidades`;

  // Cadastra um novo horário de disponibilidade
  cadastrar(dto: { dataHoraInicio: string; dataHoraFim: string }): Observable<any> {
    return this.http.post(this.apiUrl, dto);
  }

  // Lista as disponibilidades futuras de um mentor específico
  listarPorMentor(mentorId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/mentor/${mentorId}`);
  }

  listarMinhas(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/minhas`);
  }
}
