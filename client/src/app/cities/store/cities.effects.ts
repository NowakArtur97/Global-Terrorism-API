import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { map, switchMap } from 'rxjs/operators';

import CitiesGetResponse from '../models/cities-get-response.model';
import * as CitiesActions from './cities.actions';

@Injectable()
export default class CitiesEffects {
  constructor(private actions$: Actions, private httpClient: HttpClient) {}

  fetchRecipes$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CitiesActions.fetchCitites),
      switchMap(() => {
        const headers = new HttpHeaders({
          'Content-Type': 'application/json',
          Authorization: `Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImV4cCI6MTYwMDM2NTc4OCwiaWF0IjoxNjAwMzI5Nzg4fQ.WJs7GpX5x7uLUTgNLLAXXRXJLq22kX2c9fbN48-PbUM`,
        });

        return this.httpClient.get<CitiesGetResponse>(
          'http://localhost:8080/api/v1/cities?page=0&size=20',
          { headers: headers }
        );
      }),
      map((response) => {
        console.log('CitiesEffects: ');
        console.log(response);
        console.log(response.content);
        return response.content.cities;
      }),
      map((cities) => CitiesActions.setCities({ cities }))
    )
  );
}
