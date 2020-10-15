import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import GenericRestService from 'src/app/shared/services/generic-rest.service';

import EventsGetResponse from '../models/events-get-response.model';

@Injectable({ providedIn: 'root' })
export default class EventService extends GenericRestService<
  EventsGetResponse
> {
  constructor(protected httpClient: HttpClient) {
    super(httpClient, 'events');
  }
}
