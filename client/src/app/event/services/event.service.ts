import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import User from 'src/app/auth/models/user.model';
import BulkRequestMethod from 'src/app/common/models/bulk-request-method.model';
import BulkRequest from 'src/app/common/models/bulk-request.model';
import GenericRestService from 'src/app/common/services/generic-rest.service';
import { environment } from 'src/environments/environment';

import EventDTO from '../models/event.dto';
import Event from '../models/event.model';
import EventsGetResponse from '../models/events-get-response.model';

@Injectable({ providedIn: 'root' })
export default class EventService extends GenericRestService<EventsGetResponse> {
  private DEFAULT_DEPTH_FOR_EVENTS = 2;
  private DEFAULT_DEPTH_FOR_ONE_EVENT = 5;

  constructor(protected httpClient: HttpClient) {
    super(httpClient, 'events');
  }

  get(
    id: number,
    depth: number = this.DEFAULT_DEPTH_FOR_ONE_EVENT
  ): Observable<Event> {
    if (depth < 0) {
      depth = 0;
    } else if (depth > this.DEFAULT_DEPTH_FOR_ONE_EVENT) {
      depth = this.DEFAULT_DEPTH_FOR_ONE_EVENT;
    }
    return this.httpClient.get<Event>(
      `${environment.baseApiUrl}/${this.actionUrl}/${id}/depth/${depth}`
    );
  }

  getAll(
    pageSize: number = this.DEFAULT_PAGE_SIZE,
    depth: number = this.DEFAULT_DEPTH_FOR_EVENTS
  ): Observable<EventsGetResponse> {
    return this.httpClient.get<EventsGetResponse>(
      `${environment.baseApiUrl}/${this.actionUrl}/depth/${depth}?page=0&size=${pageSize}`
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

  deleteAll(events: Event[], user: User): Observable<{}> {
    if (events.length === 0) {
      return;
    }
    const eventsUrl = '/api/v1/events';
    const body: BulkRequest = { operations: [] };
    events.forEach((event) => {
      const requestMethod: BulkRequestMethod = {
        method: 'DELETE',
        url: `${eventsUrl}/${event.id}`,
        headers: { Authorization: `Bearer ${user.token}` },
      };
      body.operations.push(requestMethod);
    });

    return this.httpClient.post<BulkRequest>(
      `${environment.baseApiUrl}/${environment.bulkRequestUrl}`,
      body
    );
  }
}
