import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Financial } from '../models/financeiro.model';

export interface FinancialStats {
  total: number;
  revenues: number;
  expenses: number;
  balance: number;
}


@Injectable({
  providedIn: 'root'
})
export class FinancialService {

  private readonly API = 'http://localhost:8080/financial-transactions';

  constructor(private http: HttpClient) {}

  list(): Observable<Financial[]> {
    return this.http.get<Financial[]>(this.API);
  }

  getStats(): Observable<FinancialStats> {
    return this.http.get<FinancialStats>(`${this.API}/stats`);
  }
}
