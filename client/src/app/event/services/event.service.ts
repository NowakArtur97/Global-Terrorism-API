import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import GenericRestService from 'src/app/common/services/generic-rest.service';
import { environment } from 'src/environments/environment';

import EventDTO from '../models/event.dto';
import EventsGetResponse from '../models/events-get-response.model';

@Injectable({ providedIn: 'root' })
export default class EventService extends GenericRestService<
  EventsGetResponse
> {
  constructor(protected httpClient: HttpClient) {
    super(httpClient, 'events');
  }

  add(event: EventDTO): void {
    this.httpClient.post<Event>(
      `${environment.baseApiUrl}/${this.actionUtl}`,
      event
    );
  }
}
