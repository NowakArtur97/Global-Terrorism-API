import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import AuthResponse from '../models/AuthResponseData';
import LoginData from '../models/LoginData';
import RegistrationData from '../models/RegistrationData';
import User from '../models/User';

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
    return this.httpClient.post<AuthResponse>(`${this.BASE_URL}/registration`, {
      userName,
      email,
      password,
      matchingPassword,
    });
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
