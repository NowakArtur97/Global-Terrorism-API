import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { getTestBed, TestBed } from '@angular/core/testing';

import AuthResponse from '../models/AuthResponseData';
import LoginData from '../models/LoginData';
import RegistrationData from '../models/RegistrationData';
import User from '../models/User';
import AuthService from './auth.service';

describe('AuthService', () => {
  let injector: TestBed;
  let authService: AuthService;
  let httpMock: HttpTestingController;

  const BASE_URL = 'http://localhost:8080/api/v1';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService],
    });
  });

  beforeEach(() => {
    injector = getTestBed();
    authService = injector.inject(AuthService);
    httpMock = injector.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('loginUser$', () => {
    it('should login user', () => {
      const loginData = new LoginData('username', 'password');
      const authResponse = new AuthResponse('token');

      authService.loginUser(loginData).subscribe((res) => {
        expect(res).toEqual(authResponse);
      });

      const req = httpMock.expectOne(`${BASE_URL}/authentication`);
      expect(req.request.method).toBe('POST');
      req.flush(authResponse);
    });
  });

  describe('registerUser$', () => {
    it('should register user', () => {
      const registrationData = new RegistrationData(
        'username',
        'email@email.com',
        'pass',
        'pass'
      );
      const authResponse = new AuthResponse('token');

      authService.registerUser(registrationData).subscribe((res) => {
        expect(res).toEqual(authResponse);
      });

      const req = httpMock.expectOne(`${BASE_URL}/registration`);
      expect(req.request.method).toBe('POST');
      req.flush(authResponse);
    });
  });

  describe('getUserFromLocalStorage$', () => {
    it('should get user from local storage when user data is stored in local storage', () => {
      const userData: {
        _token: string;
      } = { _token: 'secret token' };
      const userExpected = new User('secret token');

      spyOn(localStorage, 'getItem').and.callFake(() =>
        JSON.stringify(userData)
      );
      const userActual = authService.getUserFromLocalStorage();

      expect(userActual).toEqual(userExpected);
      expect(localStorage.getItem).toHaveBeenCalled();
    });

    it('should return null when user data is not stored in local storage', () => {
      const userActual = authService.getUserFromLocalStorage();

      expect(userActual).toBeNull();
    });
  });

  describe('removeUserFromLocalStorage$', () => {
    it('should remove user from local storage', () => {
      spyOn(localStorage, 'removeItem').and.callThrough();

      authService.removeUserFromLocalStorage();

      expect(localStorage.removeItem).toHaveBeenCalled();
    });
  });

  describe('saveUserInLocalStorage$', () => {
    it('should save user in local storage', () => {
      const user = new User('secret token');

      spyOn(localStorage, 'setItem').and.callFake(() => JSON.stringify(user));

      authService.saveUserInLocalStorage(user);

      expect(localStorage.setItem).toHaveBeenCalled();
    });
  });
});
