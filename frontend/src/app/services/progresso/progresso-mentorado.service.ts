import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ProgressoResponse {
  id: number;
  mentoradoId: number;
  mentorNome: string;
  descricao: string;
  dataRegistro: string;
}

@Injectable({ providedIn: 'root' })
export class ProgressoService {
  private http = inject(HttpClient);
  private url = 'http://localhost:8080/api/progresso';

  registrar(mentoradoId: number, descricao: string): Observable<ProgressoResponse> {
    return this.http.post<ProgressoResponse>(this.url, { mentoradoId, descricao });
  }

  atualizar(id: number, descricao: string): Observable<ProgressoResponse> {
    return this.http.put<ProgressoResponse>(`${this.url}/${id}`, descricao);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }

  listarPorMentorado(mentoradoId: number): Observable<ProgressoResponse[]> {
    return this.http.get<ProgressoResponse[]>(`${this.url}/mentorado/${mentoradoId}`);
  }
}
