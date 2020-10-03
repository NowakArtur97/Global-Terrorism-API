import { Injectable } from '@angular/core';
import { AsyncValidatorFn, FormGroup, ValidationErrors } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { catchError, first, map } from 'rxjs/operators';

import RegistrationCheckRequest from '../../models/registration-check-request.model';
import AuthService from '../../services/auth.service';

@Injectable({ providedIn: 'root' })
export default class UserDataValidators {
  static userDataAlreadyTaken(authService: AuthService): AsyncValidatorFn {
    return (formGroup: FormGroup): Observable<ValidationErrors> => {
      const userNameControl = formGroup.controls.userName;
      const emailControl = formGroup.controls.email;
      return authService
        .checkUserData(
          new RegistrationCheckRequest(
            userNameControl.value,
            emailControl.value
          )
        )
        .pipe(
          map((response) => {
            const { isUserNameAvailable, isEmailAvailable } = response;
            if (!isUserNameAvailable) {
              userNameControl.setErrors({
                ...userNameControl.errors,
                userNameAlreadyTaken: true,
              });
            }
            if (!isEmailAvailable) {
              emailControl.setErrors({
                ...emailControl.errors,
                emailAlreadyTaken: true,
              });
            }

            return null;
          }),
          first(),
          catchError((error) => {
            console.log(error);
            return of({
              ...formGroup.errors,
              unknownError: true,
            });
          })
        );
    };
  }
}
