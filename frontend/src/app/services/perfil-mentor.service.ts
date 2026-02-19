import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PerfilMentor } from '../models/PerfilMentor';

@Injectable({
  providedIn: 'root'
})
export class PerfilMentorService {

  private apiUrl = 'http://localhost:8080/api/mentors';

  constructor(private http: HttpClient) {}

  buscarMentores(filtros: any): Observable<PerfilMentor[]> {

    let params = new HttpParams();

    Object.keys(filtros).forEach(key => {
      if (filtros[key]) {
        params = params.set(key, filtros[key]);
      }
    });

    return this.http.get<PerfilMentor[]>(this.apiUrl, { params });
  }
}