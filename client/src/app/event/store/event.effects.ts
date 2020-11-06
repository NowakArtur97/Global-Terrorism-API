import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { map, switchMap } from 'rxjs/operators';

import EventService from '../services/event.service';
import * as EventActions from './event.actions';

@Injectable()
export default class EventEffects {
  constructor(private actions$: Actions, private eventService: EventService) {}

  fetchEvents$ = createEffect(() =>
    this.actions$.pipe(
      ofType(EventActions.fetchEvents),
      switchMap(() => this.eventService.getAll()),
      map((response) => response.content),
      map((events) => EventActions.setEvents({ events }))
    )
  );

  addEvent$ = createEffect(() =>
    this.actions$.pipe(
      ofType(EventActions.addEventStart),
      switchMap((action) =>
        this.eventService
          .add(action.event)
          .pipe(map((event) => EventActions.addEvent({ event })))
      )
    )
  );

  updateEventStart$ = createEffect(() =>
    this.actions$.pipe(
      ofType(EventActions.updateEventStart),
      switchMap((action) =>
        this.eventService.get(action.id).pipe(
          map((eventToUpdate) =>
            EventActions.updateEvent({
              eventToUpdate,
            })
          )
        )
      )
    )
  );
}
// event: { id: event.id, changes: event },
