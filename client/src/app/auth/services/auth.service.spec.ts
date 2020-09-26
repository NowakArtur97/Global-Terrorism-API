import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { getTestBed, TestBed } from '@angular/core/testing';

import AuthResponse from '../models/AuthResponseData';
import LoginData from '../models/LoginData';
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
});
