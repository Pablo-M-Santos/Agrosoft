import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AnimalType } from '../models/animal-type.model';

@Injectable({
  providedIn: 'root',
})
export class AnimalTypeService {
  private readonly API = 'http://localhost:8080/animal-types';

  constructor(private http: HttpClient) {}

  list(): Observable<AnimalType[]> {
    return this.http.get<AnimalType[]>(this.API);
  }
}
