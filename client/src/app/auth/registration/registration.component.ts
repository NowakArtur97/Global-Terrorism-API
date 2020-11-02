import { Component, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { Subscription } from 'rxjs';
import CommonValidators from 'src/app/common/validators/common.validator';
import AppStoreState from 'src/app/store/app.store.state';

import RegistrationData from '../models/registration-data.model';
import AuthService from '../services/auth.service';
import * as AuthActions from '../store/auth.actions';
import PasswordValidators from './validators/password.validator';
import UserDataValidators from './validators/user-data.validator';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css'],
})
export class RegistrationComponent implements OnInit, OnDestroy {
  private authErrorsSubscription$: Subscription;
  private registerFormSubscriptions$ = new Subscription();
  registerForm: FormGroup;
  authErrors: string[] = [];
  isLoading = false;

  constructor(
    private store: Store<AppStoreState>,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.authErrorsSubscription$ = this.store
      .select('auth')
      .subscribe((authState) => {
        this.authErrors = authState.authErrorMessages;
        this.isLoading = authState.isLoading;
      });

    this.initForm();
  }

  ngOnDestroy(): void {
    this.authErrorsSubscription$?.unsubscribe();
    this.registerFormSubscriptions$.unsubscribe();
  }

  initForm(): void {
    this.registerForm = new FormGroup(
      {
        userName: new FormControl('', [
          Validators.minLength(5),
          Validators.maxLength(20),
          CommonValidators.notBlank,
        ]),
        email: new FormControl('', [
          Validators.email,
          CommonValidators.notBlank,
        ]),
        password: new FormControl('', [
          Validators.minLength(7),
          Validators.maxLength(30),
          CommonValidators.notBlank,
          CommonValidators.withoutSpaces,
          CommonValidators.notThreeRepetitiveCharacters,
          PasswordValidators.notPopular,
          PasswordValidators.characteristicRule,
        ]),
        matchingPassword: new FormControl('', [
          Validators.minLength(7),
          Validators.maxLength(30),
          CommonValidators.notBlank,
          CommonValidators.withoutSpaces,
          CommonValidators.notThreeRepetitiveCharacters,
          PasswordValidators.notPopular,
          PasswordValidators.characteristicRule,
        ]),
      },
      {
        validators: [
          CommonValidators.notInclude('password', 'userName'),
          CommonValidators.notInclude('matchingPassword', 'userName'),
          CommonValidators.notMatch('password', 'matchingPassword'),
        ],
        asyncValidators: [
          UserDataValidators.userDataAlreadyTaken(this.authService),
        ],
      }
    );
    this.setupFormSubscriptions();
  }

  setupFormSubscriptions(): void {
    this.registerFormSubscriptions$.add(
      this.userName.valueChanges.subscribe(() => {
        this.email.updateValueAndValidity({
          emitEvent: false,
        });
        this.password.updateValueAndValidity({
          emitEvent: false,
        });
        this.matchingPassword.updateValueAndValidity({
          emitEvent: false,
        });
      })
    );
    this.registerFormSubscriptions$.add(
      this.email.valueChanges.subscribe(() => {
        this.userName.updateValueAndValidity({
          emitEvent: false,
        });
      })
    );
    this.registerFormSubscriptions$.add(
      this.password.valueChanges.subscribe(() =>
        this.matchingPassword.updateValueAndValidity({
          emitEvent: false,
        })
      )
    );
    this.registerFormSubscriptions$.add(
      this.matchingPassword.valueChanges.subscribe(() =>
        this.password.updateValueAndValidity({
          emitEvent: false,
        })
      )
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
