import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { Actions, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { Observable, of } from 'rxjs';
import { switchMap, take, withLatestFrom } from 'rxjs/operators';

import AppStoreState from '../store/app.state';
import Event from './models/event.model';
import * as EventActions from './store/event.actions';
import { selectAllEvents } from './store/event.reducer';

@Injectable({ providedIn: 'root' })
export default class EventResolver implements Resolve<{ events: Event[] }> {
  constructor(private actions$: Actions, private store: Store<AppStoreState>) {}

  resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<{
    events: Event[];
  }> {
    return this.store.select('auth').pipe(
      withLatestFrom(this.store.select(selectAllEvents)),
      take(1),
      switchMap((stores) => {
        const isAuth = !!stores[0].user;
        const events = stores[1];
        if (isAuth && events?.length === 0) {
          this.store.dispatch(EventActions.fetchEvents());
          return this.actions$.pipe(ofType(EventActions.setEvents), take(1));
        } else {
          return of({ events });
        }
      })
    );
  }
}
