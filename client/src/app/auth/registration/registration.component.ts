import { Component, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import CommonValidators from 'src/app/shared/validators/common.validator';
import AppStoreState from 'src/app/store/app.store.state';

import RegistrationData from '../models/RegistrationData';
import * as AuthActions from '../store/auth.actions';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css'],
})
export class RegistrationComponent implements OnInit, OnDestroy {
  registerForm: FormGroup;
  authErrors: string[] = [];
  private authErrorsSubscription: Subscription;
  private passwordChangesSubscription: Subscription;

  constructor(private store: Store<AppStoreState>) {}

  ngOnInit(): void {
    this.initForm();

    this.authErrorsSubscription = this.store
      .select('auth')
      .pipe(map((authState) => authState.authErrorMessages))
      .subscribe((authErrorMessages) => (this.authErrors = authErrorMessages));
  }

  ngOnDestroy(): void {
    this.authErrorsSubscription?.unsubscribe();
    this.passwordChangesSubscription?.unsubscribe();
  }

  initForm(): void {
    this.registerForm = new FormGroup({
      userName: new FormControl('', [
        Validators.minLength(5),
        Validators.maxLength(20),
        CommonValidators.notBlank,
      ]),
      email: new FormControl('', [Validators.email, CommonValidators.notBlank]),
      password: new FormControl('', [
        CommonValidators.notBlank,
        CommonValidators.withoutSpaces,
      ]),
      matchingPassword: new FormControl('', [
        CommonValidators.notBlank,
        CommonValidators.withoutSpaces,
      ]),
    });
    this.registerForm.setValidators([
      CommonValidators.notInclude('password', 'userName'),
      CommonValidators.notInclude('matchingPassword', 'userName'),
      CommonValidators.notMatch('password', 'matchingPassword'),
    ]);

    this.passwordChangesSubscription = this.registerForm.controls.password.valueChanges.subscribe(
      () => {
        this.registerForm.controls.matchingPassword.updateValueAndValidity();
      }
    );
  }

  onRegister(): void {
    const {
      userName,
      email,
      password,
      matchingPassword,
    } = this.registerForm.value;
    this.store.dispatch(
      AuthActions.registerUserStart({
        registrationData: new RegistrationData(
          userName,
          email,
          password,
          matchingPassword
        ),
      })
    );
  }

  get userName(): AbstractControl {
    return this.registerForm.get('userName');
  }

  get email(): AbstractControl {
    return this.registerForm.get('email');
  }

  get password(): AbstractControl {
    return this.registerForm.get('password');
  }

  get matchingPassword(): AbstractControl {
    return this.registerForm.get('matchingPassword');
  }
}
