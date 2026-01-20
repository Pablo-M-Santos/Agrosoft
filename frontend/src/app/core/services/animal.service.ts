import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Animal } from '../models/animal.model';

export interface AnimalStats {
  total: number;
  active: number;
  sold: number;
  underCare: number;
}

@Injectable({
  providedIn: 'root'
})
export class AnimalService {

  private readonly API = 'http://localhost:8080/animals';

  constructor(private http: HttpClient) {}

  list(): Observable<Animal[]> {
    return this.http.get<Animal[]>(this.API);
  }

  getStats(): Observable<AnimalStats> {
    return this.http.get<AnimalStats>(`${this.API}/stats`);
  }

  findByEmployee(employeeId: string): Observable<Animal[]> {
    return this.http.get<Animal[]>(`${this.API}/employee/${employeeId}`);
  }

  findByType(typeId: string): Observable<Animal[]> {
    return this.http.get<Animal[]>(`${this.API}/type/${typeId}`);
  }
}
