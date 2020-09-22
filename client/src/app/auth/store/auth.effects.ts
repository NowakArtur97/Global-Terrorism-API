import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Actions } from '@ngrx/effects';

@Injectable()
export default class AuthEffects {
  constructor(private actions$: Actions, private httpClient: HttpClient) {}

  // authLogin$ = createEffect(() =>
  //   this.actions$.pipe(
  //     ofType(AuthActions.loginUser),
  //     switchMap((action) => {
  //       return this.httpClient.post<AuthResponse>(
  //         'http://localhost:8080/api/v1/authentication',
  //         {
  //           user: action.userNameOrEmail,
  //           email: action.userNameOrEmail,
  //           password: action.password,
  //         }
  //       );
  //     })
  //   )
  // );
}
