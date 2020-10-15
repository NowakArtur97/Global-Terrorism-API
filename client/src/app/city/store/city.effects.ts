import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { map, switchMap } from 'rxjs/operators';

import CityService from '../services/city.service';
import * as CityActions from './city.actions';

@Injectable()
export default class CityEffects {
  constructor(private actions$: Actions, private cityService: CityService) {}

  fetchCities$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CityActions.fetchCities),
      switchMap(() => this.cityService.getAll()),
      map((response) => response.content),
      map((cities) => CityActions.setCities({ cities }))
    )
  );
}
