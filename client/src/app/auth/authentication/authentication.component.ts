import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import AppStoreState from 'src/app/store/app.store.state';

import LoginData from '../models/LoginData';
import * as AuthActions from '../store/auth.actions';

@Component({
  selector: 'app-authentication',
  templateUrl: './authentication.component.html',
  styleUrls: ['./authentication.component.css'],
})
export class AuthenticationComponent implements OnInit, OnDestroy {
  loginForm: FormGroup;
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

  initForm() {
    this.loginForm = new FormGroup({
      userNameOrEmail: new FormControl('', Validators.required),
      password: new FormControl('', Validators.required),
    });
  }

  onLogin() {
    const { userNameOrEmail, password } = this.loginForm.value;
    this.store.dispatch(
      AuthActions.loginUserStart({
        loginData: new LoginData(userNameOrEmail, password),
      })
    );
  }

  get userNameOrEmail() {
    return this.loginForm.get('userNameOrEmail');
  }

  get password() {
    return this.loginForm.get('password');
  }
}
