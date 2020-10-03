import { Component, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import CommonValidators from 'src/app/shared/validators/common.validator';
import AppStoreState from 'src/app/store/app.store.state';

import AuthService from '../services/auth.service';
import PasswordValidators from './validators/password.validator';
import UserDataValidators from './validators/user-data.validator';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css'],
})
export class RegistrationComponent implements OnInit, OnDestroy {
  registerForm: FormGroup;
  authErrors: string[] = [];
  private authErrorsSubscription: Subscription;
  private userNameSubscription: Subscription;
  private passwordChangesSubscription: Subscription;
  private matchingPasswordChangesSubscription: Subscription;
  private userEmailSubscription: Subscription;

  constructor(
    private store: Store<AppStoreState>,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.authErrorsSubscription = this.store
      .select('auth')
      .pipe(map((authState) => authState.authErrorMessages))
      .subscribe((authErrorMessages) => (this.authErrors = authErrorMessages));

    this.initForm();
  }

  ngOnDestroy(): void {
    this.authErrorsSubscription?.unsubscribe();
    this.userNameSubscription?.unsubscribe();
    this.matchingPasswordChangesSubscription?.unsubscribe();
    this.passwordChangesSubscription?.unsubscribe();
    this.userEmailSubscription?.unsubscribe();
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
    this.userNameSubscription = this.registerForm.controls.userName.valueChanges.subscribe(
      () => {
        this.email.updateValueAndValidity({
          emitEvent: false,
        });
        this.password.updateValueAndValidity({
          emitEvent: false,
        });
        this.matchingPassword.updateValueAndValidity({
          emitEvent: false,
        });
      }
    );
    this.userEmailSubscription = this.registerForm.controls.email.valueChanges.subscribe(
      () => {
        this.userName.updateValueAndValidity({
          emitEvent: false,
        });
      }
    );
    this.passwordChangesSubscription = this.registerForm.controls.password.valueChanges.subscribe(
      () =>
        this.matchingPassword.updateValueAndValidity({
          emitEvent: false,
        })
    );
    this.matchingPasswordChangesSubscription = this.registerForm.controls.matchingPassword.valueChanges.subscribe(
      () =>
        this.password.updateValueAndValidity({
          emitEvent: false,
        })
    );
  }

  onRegister(): void {
    const {
      userName,
      email,
      password,
      matchingPassword,
    } = this.registerForm.value;
    console.log(this.registerForm.errors);
    // this.store.dispatch(
    //   AuthActions.registerUserStart({
    //     registrationData: new RegistrationData(
    //       userName,
    //       email,
    //       password,
    //       matchingPassword
    //     ),
    //   })
    // );
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
