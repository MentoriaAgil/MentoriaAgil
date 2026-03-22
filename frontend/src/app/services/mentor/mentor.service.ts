import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { MentorDTO } from '../../models/Mentor';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MentorService {
  private http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/mentors`;

  createProfile(data: MentorDTO): Observable<any> {
    return this.http.post(this.API_URL, data);
  }
}
