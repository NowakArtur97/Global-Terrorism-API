import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';
import ErrorResponse from 'src/app/common/models/error-response.model';

import AuthResponse from '../models/auth-response.model';
import User from '../models/user.model';
import AuthService from '../services/auth.service';
import * as AuthActions from './auth.actions';

@Injectable()
export default class AuthEffects {
  constructor(private actions$: Actions, private authService: AuthService) {}

  loginUser$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.loginUserStart),
      switchMap((action) =>
        this.authService.loginUser(action.loginData).pipe(
          map((responseData) => this.handleAuthentication(responseData)),
          catchError((errorResponse) => this.handleError(errorResponse.error))
        )
      )
    )
  );

  registerUser$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.registerUserStart),
      switchMap((action) =>
        this.authService.registerUser(action.registrationData).pipe(
          map((responseData) => this.handleAuthentication(responseData)),
          catchError((errorResponse) => this.handleError(errorResponse.error))
        )
      )
    )
  );

  autoUserLogin$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.autoUserLogin),
      map(() => {
        const user = this.authService.getUserFromLocalStorage();
        if (user) {
          this.authService.setLogoutTimer(user.expirationDate.toString());
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
        tap(() => {
          this.authService.clearLogoutTimer();
          this.authService.removeUserFromLocalStorage();
        })
      ),
    { dispatch: false }
  );

  private handleAuthentication = (authResponse: AuthResponse) => {
    const { token, expirationTimeInMilliseconds } = authResponse;
    const expirationDate = new Date(Date.now() + expirationTimeInMilliseconds);
    const user = new User(token, expirationDate);
    this.authService.setLogoutTimer(expirationDate.toString());
    this.authService.saveUserInLocalStorage(user);
    return AuthActions.authenticateUserSuccess({
      user,
    });
  };

  private handleError = (errorResponse: ErrorResponse) => {
    const authErrorMessages = errorResponse?.errors || ['Unknown error.'];
    return of(
      AuthActions.authenticateUserFailure({
        authErrorMessages,
      })
    );
  };
}
