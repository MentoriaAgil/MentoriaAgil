// app/services/perfil-mentor.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PerfilMentor } from '../models/PerfilMentor';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class PerfilMentorService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/mentors`;

  // Busca o perfil associado ao ID do usuário logado
  getPerfilByUserId(userId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/user/${userId}`);
  }

  // Atualiza Bio e Especialidade
  updatePerfil(id: number, perfil: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, perfil);
  }

  buscarMentores(filtros: any): Observable<PerfilMentor[]> {
    let params = new HttpParams();
    Object.keys(filtros).forEach((key) => {
      if (filtros[key]) params = params.set(key, filtros[key]);
    });
    return this.http.get<PerfilMentor[]>(this.apiUrl, { params });
  }
}
