import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import AppStoreState from 'src/app/store/app.store.state';

import LoginData from '../models/LoginData';
import * as AuthActions from '../store/auth.actions';

@Component({
  selector: 'app-authentication',
  templateUrl: './authentication.component.html',
  styleUrls: ['./authentication.component.css'],
})
export class AuthenticationComponent implements OnInit {
  loginForm: FormGroup;

  constructor(private store: Store<AppStoreState>) {}

  ngOnInit(): void {
    this.initForm();
  }

  initForm() {
    this.loginForm = new FormGroup({
      userNameOrEmail: new FormControl(null, Validators.required),
      password: new FormControl(null, Validators.required),
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
}
