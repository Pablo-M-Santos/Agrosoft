import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { User } from '../models/user.model';
import { AuthResponse, AuthState, LoginPayload, RegisterPayload } from './auth.types';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly API = 'http://localhost:8080/auth';
  private readonly TOKEN_KEY = 'agrosoft_access_token';

  private readonly state$ = new BehaviorSubject<AuthState>({
    token: this.getStoredToken(),
    me: null,
  });

  constructor(private http: HttpClient) {}

  login(payload: LoginPayload): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API}/login`, payload).pipe(
      tap((response) => {
        this.setToken(response.accessToken);
      }),
    );
  }

  register(payload: RegisterPayload): Observable<User> {
    return this.http.post<User>(`${this.API}/register`, payload);
  }

  me(): Observable<User> {
    return this.http.get<User>(`${this.API}/me`).pipe(
      tap((user) => {
        this.state$.next({
          token: this.getToken(),
          me: user,
        });
      }),
    );
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    this.state$.next({ token: null, me: null });
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  getToken(): string | null {
    return this.state$.value.token || this.getStoredToken();
  }

  private setToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
    this.state$.next({ token, me: this.state$.value.me });
  }

  private getStoredToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }
}
