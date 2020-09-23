import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import ErrorResponse from 'src/app/shared/models/ErrorResponse';

import AuthResponse from '../models/AuthResponseData';
import User from '../models/User';
import * as AuthActions from './auth.actions';

const handleAuthentication = (responseData: AuthResponse) => {
  const user = new User(responseData.token);
  return AuthActions.authenticateUserSuccess({
    user,
  });
};

const handleError = (errorResponse: ErrorResponse) => {
  const authErrorMessages = errorResponse.errors;
  return of(
    AuthActions.authenticateUserFailure({
      authErrorMessages,
    })
  );
};

@Injectable()
export default class AuthEffects {
  constructor(private actions$: Actions, private httpClient: HttpClient) {}

  loginUser$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.loginUserStart),
      switchMap((action) => {
        const { userNameOrEmail, password } = action.loginData;

        return this.httpClient
          .post<AuthResponse>('http://localhost:8080/api/v1/authentication', {
            user: userNameOrEmail,
            email: userNameOrEmail,
            password,
          })
          .pipe(
            map((responseData) => handleAuthentication(responseData)),
            catchError((errorResponse) => handleError(errorResponse.error))
          );
      })
    )
  );

  registerUser$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.registerUserStart),
      switchMap((action) => {
        const {
          userName,
          email,
          password,
          matchingPassword,
        } = action.registrationData;
        return this.httpClient
          .post<AuthResponse>('http://localhost:8080/api/v1/registration', {
            userName,
            email,
            password,
            matchingPassword,
          })
          .pipe(
            map((responseData) => handleAuthentication(responseData)),
            catchError((errorResponse) => handleError(errorResponse.error))
          );
      })
    )
  );
}
