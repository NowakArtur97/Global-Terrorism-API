import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { map, switchMap } from 'rxjs/operators';

import CitiesService from '../services/cities.service';
import * as CitiesActions from './cities.actions';

@Injectable()
export default class CitiesEffects {
  constructor(
    private actions$: Actions,
    private citiesService: CitiesService
  ) {}

  fetchCities$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CitiesActions.fetchCitites),
      switchMap(() => this.citiesService.getCities()),
      map((response) => response.content),
      map((cities) => CitiesActions.setCities({ cities }))
    )
  );
}
