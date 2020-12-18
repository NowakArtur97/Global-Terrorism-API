import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export default class ShapeService {
  constructor(private httpClient: HttpClient) {}

  getCountriesShapes(): Observable<any> {
    return this.httpClient.get('./assets/data/countries.geo.json', {
      responseType: 'json',
    });
  }
}
