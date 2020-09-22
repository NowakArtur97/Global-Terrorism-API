import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';

import AuthResponse from '../models/AuthResponseData';
import User from '../models/User';
import * as AuthActions from './auth.actions';

const handleAuthentication = (responseData: AuthResponse) => {
  const user = new User(responseData.token);
  console.log(user);
  return AuthActions.authenticateUserSuccess({
    user,
  });
};

const handleError = (errorResponse: any) => {
  console.log(errorResponse);

  const user = new User('dummy');
  return of(
    AuthActions.authenticateUserSuccess({
      user,
    })
  );
};

@Injectable()
export default class AuthEffects {
  constructor(private actions$: Actions, private httpClient: HttpClient) {}

  authLogin$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.loginUserStart),
      switchMap((action) => {
        console.log(action.loginData.userNameOrEmail);
        return this.httpClient
          .post<AuthResponse>('http://localhost:8080/api/v1/authentication', {
            user: action.loginData.userNameOrEmail,
            email: action.loginData.userNameOrEmail,
            password: action.loginData.password,
          })
          .pipe(
            tap((responseData) => {
              console.log(responseData);
            }),
            map((responseData) => handleAuthentication(responseData)),
            catchError((errorResponse) => handleError(errorResponse))
          );
      })
    )
  );
}
