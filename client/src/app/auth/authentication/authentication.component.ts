import { Component, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { Subscription } from 'rxjs';
import AppStoreState from 'src/app/store/app.store.state';

import LoginData from '../models/login-data.model';
import * as AuthActions from '../store/auth.actions';

@Component({
  selector: 'app-authentication',
  templateUrl: './authentication.component.html',
  styleUrls: ['./authentication.component.css'],
})
export class AuthenticationComponent implements OnInit, OnDestroy {
  private authErrorsSubscription$: Subscription;
  loginForm: FormGroup;
  authErrors: string[] = [];
  isLoading = false;

  constructor(private store: Store<AppStoreState>) {}

  ngOnInit(): void {
    this.initForm();

    this.authErrorsSubscription$ = this.store
      .select('auth')
      .subscribe((authState) => {
        this.authErrors = authState.authErrorMessages;
        this.isLoading = authState.isLoading;
      });
  }

  ngOnDestroy(): void {
    this.authErrorsSubscription$.unsubscribe();
  }

  initForm(): void {
    this.loginForm = new FormGroup({
      userNameOrEmail: new FormControl('', Validators.required),
      password: new FormControl('', Validators.required),
    });
  }

  onLogin(): void {
    const { userNameOrEmail, password } = this.loginForm.value;
    this.store.dispatch(
      AuthActions.loginUserStart({
        loginData: new LoginData(userNameOrEmail, password),
      })
    );
  }

  get userNameOrEmail(): AbstractControl {
    return this.loginForm.get('userNameOrEmail');
  }

  get password(): AbstractControl {
    return this.loginForm.get('password');
  }
}
