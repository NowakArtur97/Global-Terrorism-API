import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import GenericRestService from 'src/app/common/services/generic-rest.service';

import ProvincesGetResponse from '../models/provinces-get-response.model';

@Injectable({ providedIn: 'root' })
export default class ProvinceService extends GenericRestService<ProvincesGetResponse> {
  constructor(protected httpClient: HttpClient) {
    super(httpClient, 'provinces');
  }
}
