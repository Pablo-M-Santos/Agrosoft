import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CreateUserPayload, UpdateUserPayload, User } from '../models/user.model';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private readonly API = 'http://localhost:8080/users';

  constructor(private http: HttpClient) {}

  list(): Observable<User[]> {
    return this.http.get<User[]>(this.API);
  }

  getById(id: string): Observable<User> {
    return this.http.get<User>(`${this.API}/${id}`);
  }

  create(payload: CreateUserPayload): Observable<User> {
    return this.http.post<User>(this.API, payload);
  }

  update(id: string, payload: UpdateUserPayload): Observable<User> {
    return this.http.put<User>(`${this.API}/${id}`, payload);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API}/${id}`);
  }
}
