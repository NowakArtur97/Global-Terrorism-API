import { HttpResponseBase } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { of } from 'rxjs';
import { catchError, map, switchMap, withLatestFrom } from 'rxjs/operators';
import ErrorResponse from 'src/app/common/models/error-response.model';
import AppStoreState from 'src/app/store/app.state';
import { environment } from 'src/environments/environment.testing';

import EventService from '../services/event.service';
import * as EventActions from './event.actions';

@Injectable()
export default class EventEffects {
  constructor(
    private actions$: Actions,
    private store$: Store<AppStoreState>,
    private eventService: EventService
  ) {}

  fetchEvents$ = createEffect(() =>
    this.actions$.pipe(
      ofType(EventActions.fetchEvents),
      switchMap(() => this.eventService.getAll()),
      map((response) => response.content),
      map((events) => EventActions.setEvents({ events })),
      catchError((errorResponse) => this.handleError(errorResponse.error))
    )
  );

  addEvent$ = createEffect(() =>
    this.actions$.pipe(
      ofType(EventActions.addEventStart),
      switchMap(({ eventDTO }) =>
        this.eventService.add(eventDTO).pipe(
          map((event) => EventActions.addEvent({ event })),
          catchError((errorResponse) => this.handleError(errorResponse.error))
        )
      )
    )
  );

  updateEventStart$ = createEffect(() =>
    this.actions$.pipe(
      ofType(EventActions.updateEventStart),
      switchMap(({ id }) =>
        this.eventService.get(id).pipe(
          map((eventToUpdate) =>
            EventActions.updateEventFetch({
              eventToUpdate,
            })
          ),
          catchError((errorResponse) => this.handleError(errorResponse.error))
        )
      )
    )
  );

  updateEvent$ = createEffect(() =>
    this.actions$.pipe(
      ofType(EventActions.updateEvent),
      switchMap(({ eventDTO }) =>
        this.eventService.update(eventDTO).pipe(
          map((eventUpdated) =>
            EventActions.updateEventFinish({
              eventUpdated,
            })
          ),
          catchError((errorResponse) => this.handleError(errorResponse.error))
        )
      )
    )
  );

  deleteEventStart$ = createEffect(() =>
    this.actions$.pipe(
      ofType(EventActions.deleteEventStart),
      switchMap(({ eventToDelete }) =>
        this.eventService.delete(eventToDelete.id).pipe(
          map(() =>
            EventActions.deleteEvent({
              eventDeleted: eventToDelete,
            })
          ),
          catchError((errorResponse) => this.handleError(errorResponse.error))
        )
      )
    )
  );

  deleteEventsStart$ = createEffect(() =>
    this.actions$.pipe(
      ofType(EventActions.deleteEventsStart),
      withLatestFrom(this.store$.select((state) => state.auth.user)),
      switchMap(([action, user]) =>
        this.eventService.deleteAll(action.eventsToDelete, user).pipe(
          map(() =>
            EventActions.deleteEvents({
              eventsDeletedIds: action.eventsToDelete.map((event) => event.id),
            })
          ),
          catchError((errorResponse) => this.handleError(errorResponse.error))
        )
      )
    )
  );

  private handleError = (errorResponse: ErrorResponse | HttpResponseBase) => {
    let errorMessages: string[] = [
      'There was a problem with accessing the page. Please try again in a moment.',
    ];
    if (this.isErrorResponse(errorResponse)) {
      errorMessages = errorResponse.errors;
    } else if (this.isHttpErrorResponse(errorResponse)) {
      if (
        errorResponse + '' ===
        `Bulk operations exceed the limitation(${environment.bulkApiLimit})`
      ) {
        errorMessages = [
          `You cannot delete more than ${environment.bulkApiLimit} events at a time.`,
        ];
      }
    }

    return of(
      EventActions.httpError({
        errorMessages,
      })
    );
  };

  private isErrorResponse(response: any): response is ErrorResponse {
    return (response as ErrorResponse).errors !== undefined;
  }

  private isHttpErrorResponse(response: any): response is HttpResponseBase {
    return (response as HttpResponseBase) !== undefined;
  }
}
