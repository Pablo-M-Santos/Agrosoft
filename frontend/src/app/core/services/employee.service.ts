import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Employee } from '../models/employee.model';

export interface EmployeeStats {
  total: number;
  active: number;
  inactive: number;
  onLeave: number;
}

@Injectable({
  providedIn: 'root',
})
export class EmployeeService {
  private readonly API = 'http://localhost:8080/employees';

  constructor(private http: HttpClient) {}

  list(page: number = 0, size: number = 9, search?: string): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', 'createdAt,desc');

    if (search && search.trim()) {
      params = params.set('search', search.trim());
    }

    return this.http.get<any>(this.API, { params });
  }

  getStats(): Observable<EmployeeStats> {
    return this.http.get<EmployeeStats>(`${this.API}/stats`);
  }

  create(employee: Employee): Observable<Employee> {
    return this.http.post<Employee>(this.API, employee);
  }

  checkCpfAvailability(cpf: string, employeeId?: string): Observable<boolean> {
    let params = new HttpParams().set('cpf', cpf);

    if (employeeId) {
      params = params.set('id', employeeId);
    }

    return this.http.get<boolean>(`${this.API}/check-cpf`, { params });
  }

  checkEmailAvailability(email: string, employeeId?: string): Observable<boolean> {
    let params = new HttpParams().set('email', email);

    if (employeeId) {
      params = params.set('id', employeeId);
    }

    return this.http.get<boolean>(`${this.API}/check-email`, { params });
  }

  update(id: string, employee: Employee): Observable<Employee> {
    return this.http.put<Employee>(`${this.API}/${id}`, employee);
  }

  deactivate(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API}/${id}`);
  }

  delete(id: string): Observable<void> {
    return this.deactivate(id);
  }
}
