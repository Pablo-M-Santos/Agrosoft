import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Machine } from '../models/machine.model';

export interface MachineStats {
  total: number;
  operational: number;
  underMaintenance: number;
  inactive: number;
}

@Injectable({
  providedIn: 'root'
})
export class MachineService {

  private readonly API = 'http://localhost:8080/machines';

  constructor(private http: HttpClient) {}

  list(): Observable<Machine[]> {
    return this.http.get<Machine[]>(this.API);
  }

  getStats(): Observable<MachineStats> {
    return this.http.get<MachineStats>(`${this.API}/stats`);
  }
}
