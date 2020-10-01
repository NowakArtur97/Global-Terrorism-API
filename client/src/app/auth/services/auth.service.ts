import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import AuthResponse from '../models/auth-response.model';
import LoginData from '../models/login-data.model';
import RegistrationCheckRequest from '../models/registration-check-request.model';
import RegistrationCheckResponse from '../models/registration-check-response.model';
import RegistrationData from '../models/registration-data.model';
import User from '../models/user.model';

@Injectable({ providedIn: 'root' })
export default class AuthService {
  private BASE_URL = 'http://localhost:8080/api/v1';
  private userLocaleStorageKey = 'userData';

  constructor(private httpClient: HttpClient) {}

  loginUser(loginData: LoginData): Observable<AuthResponse> {
    const { userNameOrEmail, password } = loginData;
    return this.httpClient.post<AuthResponse>(
      `${this.BASE_URL}/authentication`,
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
      `${this.BASE_URL}/registration/register`,
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
      `${this.BASE_URL}/registration/checkUserData`,
      { userName, email }
    );
  }

  getUserFromLocalStorage(): User {
    const userData: {
      _token: string;
    } = JSON.parse(localStorage.getItem(this.userLocaleStorageKey));
    return userData?._token ? new User(userData._token) : null;
  }

  removeUserFromLocalStorage(): void {
    localStorage.removeItem(this.userLocaleStorageKey);
  }

  saveUserInLocalStorage(user: User): void {
    localStorage.setItem(this.userLocaleStorageKey, JSON.stringify(user));
  }
}
