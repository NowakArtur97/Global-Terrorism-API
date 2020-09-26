import { Component, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
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

  constructor(private store: Store<AppStoreState>) {}

  ngOnInit(): void {
    this.initForm();

    this.authErrorsSubscription = this.store
      .select('auth')
      .pipe(map((authState) => authState.authErrorMessages))
      .subscribe((authErrorMessages) => (this.authErrors = authErrorMessages));
  }

  ngOnDestroy(): void {
    this.authErrorsSubscription.unsubscribe();
  }

  initForm(): void {
    this.registerForm = new FormGroup({
      userName: new FormControl('', Validators.required),
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', Validators.required),
      matchingPassword: new FormControl('', Validators.required),
    });
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
