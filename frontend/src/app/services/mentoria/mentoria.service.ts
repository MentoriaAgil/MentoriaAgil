import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { SolicitacaoMentoriaRequest } from '../../models/SolicitacaoMentoriaRequest';
import { SolicitacaoMentoriaResponse } from '../../models/SolicitacaoMentoriaResponse';

@Injectable({
  providedIn: 'root',
})
export class MentoriaService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `http://localhost:8080/api/mentorias`;
  private readonly sessaoUrl = `http://localhost:8080/api/sessoes`;

  // Busca sessões agendadas onde o usuário logado é o mentor
  getSessoesMentor(): Observable<any[]> {
    return this.http.get<any[]>(`${this.sessaoUrl}/mentor/pendentes`);
  }

  getDisponibilidadeByMentor(mentorId: number): Observable<any[]> {
    return this.http.get<any[]>(`http://localhost:8080/api/disponibilidades/mentor/${mentorId}`);
  }

  agendarSessao(dados: any): Observable<any> {
    return this.http.post<any>(`${this.sessaoUrl}/agendar`, dados);
  }

  solicitarMentoria(dados: SolicitacaoMentoriaRequest): Observable<SolicitacaoMentoriaResponse> {
    return this.http.post<SolicitacaoMentoriaResponse>(`${this.apiUrl}/request`, dados);
  }

  getSolicitacoesRecebidas(): Observable<SolicitacaoMentoriaResponse[]> {
    return this.http.get<SolicitacaoMentoriaResponse[]>(`${this.apiUrl}/pendentes`);
  }

  atualizarStatus(
    id: number,
    status: 'ACCEPTED' | 'REJECTED',
    justificativa?: string,
  ): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}`, { status, justificativa });
  }
}
