import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Actions } from '@ngrx/effects';

@Injectable()
export default class AuthEffects {
  constructor(actions$: Actions, private httpClient: HttpClient) {}
}
