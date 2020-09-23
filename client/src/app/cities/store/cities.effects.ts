import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { map, switchMap } from 'rxjs/operators';

import CitiesGetResponse from '../models/cities-get-response.model';
import * as CitiesActions from './cities.actions';

@Injectable()
export default class CitiesEffects {
  constructor(private actions$: Actions, private httpClient: HttpClient) {}

  fetchCities$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CitiesActions.fetchCitites),
      switchMap(() => {
        return this.httpClient.get<CitiesGetResponse>(
          'http://localhost:8080/api/v1/cities?page=0&size=50'
        );
      }),
      map((response) => {
        return response.content;
      }),
      map((cities) => CitiesActions.setCities({ cities }))
    )
  );
}
