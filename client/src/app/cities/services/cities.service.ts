import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import CitiesGetResponse from '../models/cities-get-response.model';

@Injectable({ providedIn: 'root' })
export default class CitiesService {
  constructor(private httpClient: HttpClient) {}

  getCities(): Observable<CitiesGetResponse> {
    return this.httpClient.get<CitiesGetResponse>(
      'http://localhost:8080/api/v1/cities?page=0&size=50'
    );
  }
}
