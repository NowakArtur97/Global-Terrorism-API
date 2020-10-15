import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import GenericRestService from 'src/app/shared/services/generic-rest.service';

import CitiesGetResponse from '../models/cities-get-response.model';

@Injectable({ providedIn: 'root' })
export default class CityService extends GenericRestService<CitiesGetResponse> {
  constructor(protected httpClient: HttpClient) {
    super(httpClient, 'cities');
  }
}
