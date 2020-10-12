import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

import CitiesGetResponse from '../models/cities-get-response.model';

@Injectable({ providedIn: 'root' })
export default class CitiesService {
  constructor(private httpClient: HttpClient) {}

  getCities(): Observable<CitiesGetResponse> {
    return this.httpClient.get<CitiesGetResponse>(
      `${environment.baseApiUrl}/cities?page=0&size=50`
    );
  }
}
