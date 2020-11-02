import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import AppStoreState from 'src/app/store/app.state';
import { environment } from 'src/environments/environment';

import AuthResponse from '../models/auth-response.model';
import LoginData from '../models/login-data.model';
import RegistrationCheckRequest from '../models/registration-check-request.model';
import RegistrationCheckResponse from '../models/registration-check-response.model';
import RegistrationData from '../models/registration-data.model';
import User from '../models/user.model';
import * as AuthActions from '../store/auth.actions';

@Injectable({ providedIn: 'root' })
export default class AuthService {
  private userLocaleStorageKey = 'userData';
  private tokenExpirationTimer: any;

  constructor(
    private store: Store<AppStoreState>,
    private httpClient: HttpClient
  ) {}

  loginUser(loginData: LoginData): Observable<AuthResponse> {
    const { userNameOrEmail, password } = loginData;
    return this.httpClient.post<AuthResponse>(
      `${environment.baseApiUrl}/authentication`,
      {
        user: userNameOrEmail,
        email: userNameOrEmail,
        password,
      }
    );
  }

  registerUser(registrationData: RegistrationData): Observable<AuthResponse> {
    const { userName, email, password, matchingPassword } = registrationData;
    return this.httpClient.post<AuthResponse>(
      `${environment.baseApiUrl}/registration/register`,
      {
        userName,
        email,
        password,
        matchingPassword,
      }
    );
  }

  checkUserData(
    dataToCheck: RegistrationCheckRequest
  ): Observable<RegistrationCheckResponse> {
    const { userName, email } = dataToCheck;
    return this.httpClient.post<RegistrationCheckResponse>(
      `${environment.baseApiUrl}/registration/checkUserData`,
      { userName, email }
    );
  }

  getUserFromLocalStorage(): User {
    const userData: {
      _token: string;
      _expirationDate: Date;
    } = JSON.parse(localStorage.getItem(this.userLocaleStorageKey));
    return userData?._token
      ? new User(userData._token, userData._expirationDate)
      : null;
  }

  removeUserFromLocalStorage(): void {
    localStorage.removeItem(this.userLocaleStorageKey);
  }

  saveUserInLocalStorage(user: User): void {
    localStorage.setItem(this.userLocaleStorageKey, JSON.stringify(user));
  }

  setLogoutTimer(expirationDate: string): void {
    const expirationTimeInMilliseconds =
      new Date(expirationDate).getTime() - Date.now();
    this.tokenExpirationTimer = setTimeout(() => {
      this.store.dispatch(AuthActions.logoutUser());
    }, expirationTimeInMilliseconds);
  }

  clearLogoutTimer(): void {
    if (this.tokenExpirationTimer) {
      clearTimeout(this.tokenExpirationTimer);
      this.tokenExpirationTimer = null;
    }
  }
}
