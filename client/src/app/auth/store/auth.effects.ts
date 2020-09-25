import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';
import ErrorResponse from 'src/app/shared/models/ErrorResponse';

import AuthResponse from '../models/AuthResponseData';
import User from '../models/User';
import AuthService from '../services/auth.service';
import * as AuthActions from './auth.actions';

const handleAuthentication = (responseData: AuthResponse) => {
  const user = new User(responseData.token);
  localStorage.setItem('userData', JSON.stringify(user));
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
  constructor(private actions$: Actions, private authService: AuthService) {}

  loginUser$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.loginUserStart),
      switchMap((action) =>
        this.authService.loginUser(action.loginData).pipe(
          map((responseData) => handleAuthentication(responseData)),
          catchError((errorResponse) => handleError(errorResponse.error))
        )
      )
    )
  );

  registerUser$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.registerUserStart),
      switchMap((action) =>
        this.authService.registerUser(action.registrationData).pipe(
          map((responseData) => handleAuthentication(responseData)),
          catchError((errorResponse) => handleError(errorResponse.error))
        )
      )
    )
  );

  autoUserLogin$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.autoUserLogin),
      map(() => {
        const userData: {
          _token: string;
        } = JSON.parse(localStorage.getItem('userData'));

        if (userData?._token) {
          const user = new User(userData._token);
          return AuthActions.authenticateUserSuccess({
            user,
          });
        }

        return { type: 'DUMMY' };
      })
    )
  );

  logoutUser$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(AuthActions.logoutUser),
        tap(() => localStorage.removeItem('userData'))
      ),
    { dispatch: false }
  );
}
