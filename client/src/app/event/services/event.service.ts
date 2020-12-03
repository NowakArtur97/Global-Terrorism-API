import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import GenericRestService from 'src/app/common/services/generic-rest.service';
import { environment } from 'src/environments/environment';

import EventDTO from '../models/event.dto';
import Event from '../models/event.model';
import EventsGetResponse from '../models/events-get-response.model';

@Injectable({ providedIn: 'root' })
export default class EventService extends GenericRestService<EventsGetResponse> {
  private DEFAULT_DEPTH = 5;
  constructor(protected httpClient: HttpClient) {
    super(httpClient, 'events');
  }

  get(id: number, depth: number = this.DEFAULT_DEPTH): Observable<Event> {
    if (depth < 0) {
      depth = 0;
    } else if (depth > this.DEFAULT_DEPTH) {
      depth = this.DEFAULT_DEPTH;
    }
    return this.httpClient.get<Event>(
      `${environment.baseApiUrl}/${this.actionUrl}/${id}/depth/${depth}`
    );
  }

  getAll(
    pageSize: number = this.DEFAULT_PAGE_SIZE,
    depth: number = this.DEFAULT_DEPTH
  ): Observable<EventsGetResponse> {
    return this.httpClient.get<EventsGetResponse>(
      `${environment.baseApiUrl}/${this.actionUrl}?page=0&size=${pageSize}/depth/${depth}`
    );
  }

  add(event: EventDTO): Observable<Event> {
    return this.httpClient.post<Event>(
      `${environment.baseApiUrl}/${this.actionUrl}`,
      event
    );
  }

  update(event: EventDTO): Observable<Event> {
    return this.httpClient.put<Event>(
      `${environment.baseApiUrl}/${this.actionUrl}/${event.id}`,
      event
    );
  }

  delete(id: number): Observable<{}> {
    return this.httpClient.delete<{}>(
      `${environment.baseApiUrl}/${this.actionUrl}/${id}`
    );
  }
}
