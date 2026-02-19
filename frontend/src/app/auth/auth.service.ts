import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, map, tap, catchError, of } from 'rxjs';
import { jwtDecode } from 'jwt-decode';
import { User } from '../models/User';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly API_URL = 'http://localhost:8080/auth';

  private currentUserSubject = new BehaviorSubject<User | null>(this.loadUser());
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<boolean> {
    return this.http.post<{ token: string }>(`${this.API_URL}/login`, { email, password })
      .pipe(
        tap(response => this.handleAuthentication(response.token)),
        map(() => true),
        catchError(() => of(false))
      );
  }

  register(user: User): Observable<any> {
    return this.http.post(`${this.API_URL}/register`, user);
  }

  logoutNoServidor(): Observable<any> {
    return this.http.post(`${this.API_URL}/logout`, {});
  }

  logout(): void {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('auth_user');
    this.currentUserSubject.next(null);
  }

  isAuthenticated(): boolean {
  const token = this.getToken();
  if (!token) return false;

  try {
    const decoded: any = jwtDecode(token);
    const isExpired = decoded.exp * 1000 < Date.now();
    return !isExpired;
  } catch {
    return false;
  }
}

  hasRole(role: string): boolean {
    const user = this.currentUserSubject.value;
    return user?.role === role;
  }

  getToken(): string | null {
    return localStorage.getItem('auth_token');
  }

  private handleAuthentication(token: string): void {
    try{
      const decoded: any = jwtDecode(token);

      const user: User = {
        id: decoded.id,
        name: decoded.name,
        email: decoded.email,
        role: decoded.role,
        token: token
      };

      localStorage.setItem('auth_token', token);
      localStorage.setItem('auth_user', JSON.stringify(user));

      this.currentUserSubject.next(user);
    } catch (error){
      console.error('Erro ao decodificar token', error);
    }
  }

  private loadUser(): User | null {
    const storedUser = localStorage.getItem('auth_user');
    return storedUser ? JSON.parse(storedUser) : null;
  }
}
