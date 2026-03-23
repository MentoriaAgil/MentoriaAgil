import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, map, tap, catchError, of } from 'rxjs';
import { jwtDecode } from 'jwt-decode';
import { User } from '../models/User';
import { environment } from '../../environments/environment';

export type UserRole = 'ADMIN' | 'MENTOR' | 'USER' | 'VISITANTE';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly API_URL = `${environment.apiUrl}/auth`;

  private readonly TOKEN_KEY = 'auth_token';
  private readonly USER_KEY = 'auth_user';

  private currentUserSubject = new BehaviorSubject<User | null>(this.loadUser());
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<boolean> {
    return this.http.post<any>(`${this.API_URL}/login`, { email, password }).pipe(
      tap((response) => {
        this.handleAuthentication(response.token);
      }),
      map(() => true),
      catchError(() => of(false)),
    );
  }

  register(user: User): Observable<any> {
    return this.http.post(`${this.API_URL}/register`, user);
  }

  logoutNoServidor(): Observable<any> {
    return this.http.post(`${this.API_URL}/logout`, {});
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
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

  hasRole(allowedRoles: string | string[]): boolean {
    const user = this.currentUserSubject.value;
    if (!user || !user.role) return false;

    if (Array.isArray(allowedRoles)) {
      return allowedRoles.includes(user.role);
    }
    return user.role === allowedRoles;
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  private handleAuthentication(token: string): void {
    try {
      const decoded: any = jwtDecode(token);

      const user: User = {
        id: decoded.id,
        name: decoded.name,
        email: decoded.sub,
        role: decoded.role,
        token: token,
      };

      localStorage.setItem(this.TOKEN_KEY, token);
      localStorage.setItem(this.USER_KEY, JSON.stringify(user));

      this.currentUserSubject.next(user);
    } catch (error) {
      console.error('Erro ao decodificar token', error);
      this.logout();
    }
  }

  private loadUser(): User | null {
    const storedUser = localStorage.getItem(this.USER_KEY);
    return storedUser ? JSON.parse(storedUser) : null;
  }
}
