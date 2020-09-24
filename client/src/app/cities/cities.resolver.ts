import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { Actions, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { of } from 'rxjs';
import { switchMap, take, withLatestFrom } from 'rxjs/operators';

import AppStoreState from '../store/app.store.state';
import City from './models/city.model';
import * as CitiesActions from './store/cities.actions';

@Injectable({ providedIn: 'root' })
export class CitiesResolver implements Resolve<{ cities: City[] }> {
  constructor(private actions$: Actions, private store: Store<AppStoreState>) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.store.select('auth').pipe(
      withLatestFrom(this.store.select('cities')),
      take(1),
      switchMap((stores) => {
        const isAuth = !!stores[0].user;
        const cities = stores[1].cities;

        if (isAuth && cities.length === 0) {
          this.store.dispatch(CitiesActions.fetchCitites());
          return this.actions$.pipe(ofType(CitiesActions.setCities), take(1));
        } else {
          return of({ cities });
        }
      })
    );
  }
}
