import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

import EventsGetResponse from '../models/events-get-response.model';

@Injectable({ providedIn: 'root' })
export default class EventService {
  constructor(private httpClient: HttpClient) {}

  getEvents(): Observable<EventsGetResponse> {
    return this.httpClient.get<EventsGetResponse>(
      `${environment.baseApiUrl}/events?page=0&size=50`
    );
  }
}
