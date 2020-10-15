import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import RestAbstractService from 'src/app/shared/services/abstract-rest.service';

import CitiesGetResponse from '../models/cities-get-response.model';

@Injectable({ providedIn: 'root' })
export default class CityService extends RestAbstractService<
  CitiesGetResponse
> {
  constructor(protected httpClient: HttpClient) {
    super(httpClient, 'cities');
  }
}
