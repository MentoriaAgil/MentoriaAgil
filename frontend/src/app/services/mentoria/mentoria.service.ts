import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { SolicitacaoMentoriaRequest } from '../../models/SolicitacaoMentoriaRequest';
import { SolicitacaoMentoriaResponse } from '../../models/SolicitacaoMentoriaResponse';

@Injectable({
  providedIn: 'root'
})
export class MentoriaService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `http://localhost:8080/api/mentorias`;

  solicitarMentoria(dados: SolicitacaoMentoriaRequest): Observable<SolicitacaoMentoriaResponse> {
    return this.http.post<SolicitacaoMentoriaResponse>(`${this.apiUrl}/request`, dados);
  }

  // Busca solicitações pendentes para o mentor logado
  getSolicitacoesRecebidas(): Observable<SolicitacaoMentoriaResponse[]> {
    return this.http.get<SolicitacaoMentoriaResponse[]>(`${this.apiUrl}/pendentes`);
  }

  // Envia a decisão (ACCEPTED ou REJECTED)
  atualizarStatus(id: number, status: 'ACCEPTED' | 'REJECTED', justificativa?: string): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}`, { status, justificativa });
  }
}