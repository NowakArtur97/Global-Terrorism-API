import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import GenericRestService from 'src/app/common/services/generic-rest.service';

import CountriesGetResponse from '../models/countries-get-response.model';

@Injectable({ providedIn: 'root' })
export default class CountryService extends GenericRestService<CountriesGetResponse> {
  constructor(protected httpClient: HttpClient) {
    super(httpClient, 'countries');
  }
}
