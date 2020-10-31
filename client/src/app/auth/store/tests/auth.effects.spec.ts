import { HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { of, ReplaySubject, throwError } from 'rxjs';
import ErrorResponse from 'src/app/common/models/error-response.model';

import AuthResponse from '../../models/auth-response.model';
import LoginData from '../../models/login-data.model';
import RegistrationData from '../../models/registration-data.model';
import User from '../../models/user.model';
import AuthService from '../../services/auth.service';
import * as AuthActions from '../auth.actions';
import AuthEffects from '../auth.effects';

const mockLoginData = new LoginData('username', 'password');
const mockRegistrationData = new RegistrationData(
  'username',
  'email@email.com',
  'password',
  'password'
);
const mockUser = new User('secret token', new Date(Date.now() + 36000000));
const mockAuthResponse = new AuthResponse('secret token', 36000000);
const mockErrorResponse = new HttpErrorResponse({
  error: {
    errors: [new ErrorResponse(['Error message.'], 401, new Date())],
  },
  headers: new HttpHeaders('headers'),
  status: 401,
  statusText: 'OK',
  url: 'http://localhost:8080/api/v1',
});

describe('AuthEffects', () => {
  let authEffects: AuthEffects;
  let actions$: ReplaySubject<any>;
  let authService: AuthService;

  beforeEach(() =>
    TestBed.configureTestingModule({
      providers: [
        AuthEffects,
        provideMockActions(() => actions$),
        {
          provide: AuthService,
          useValue: jasmine.createSpyObj('authService', [
            'loginUser',
            'registerUser',
            'getUserFromLocalStorage',
            'removeUserFromLocalStorage',
            'saveUserInLocalStorage',
            'setLogoutTimer',
            'clearLogoutTimer',
          ]),
        },
      ],
    })
  );

  beforeEach(() => {
    authEffects = TestBed.inject(AuthEffects);
    authService = TestBed.inject(AuthService);

    (authService.setLogoutTimer as jasmine.Spy).and.callThrough();
  });

  describe('loginUser$', () => {
    beforeEach(() => {
      actions$ = new ReplaySubject(1);
      actions$.next(AuthActions.loginUserStart({ loginData: mockLoginData }));
    });

    it('should return an authenticateUserSuccess action on success', () => {
      (authService.loginUser as jasmine.Spy).and.returnValue(
        of(mockAuthResponse)
      );

      authEffects.loginUser$.subscribe((resultAction) => {
        expect(resultAction.type).toEqual('[User] Authenticate User Success');
        expect(authService.loginUser).toHaveBeenCalled();
        expect(authService.setLogoutTimer).toHaveBeenCalled();
        expect(authService.saveUserInLocalStorage).toHaveBeenCalled();
      });
    });

    it('should return authenticateUserFailure action on failure', () => {
      (authService.loginUser as jasmine.Spy).and.returnValue(
        throwError(mockErrorResponse)
      );
      (authService.setLogoutTimer as jasmine.Spy).and.callThrough();
      (authService.saveUserInLocalStorage as jasmine.Spy).and.callThrough();

      authEffects.loginUser$.subscribe((resultAction) => {
        expect(resultAction).toEqual(
          AuthActions.authenticateUserFailure({
            authErrorMessages: mockErrorResponse.error.errors,
          })
        );
        expect(authService.loginUser).toHaveBeenCalled();
      });
    });
  });

  describe('registerUser$', () => {
    beforeEach(() => {
      actions$ = new ReplaySubject(1);
      actions$.next(
        AuthActions.registerUserStart({
          registrationData: mockRegistrationData,
        })
      );
    });

    it('should return an authenticateUserSuccess action on success', () => {
      (authService.registerUser as jasmine.Spy).and.returnValue(
        of(mockAuthResponse)
      );
      (authService.setLogoutTimer as jasmine.Spy).and.callThrough();
      (authService.saveUserInLocalStorage as jasmine.Spy).and.callThrough();

      authEffects.registerUser$.subscribe((resultAction) => {
        expect(resultAction.type).toEqual('[User] Authenticate User Success');
        expect(authService.registerUser).toHaveBeenCalled();
        expect(authService.setLogoutTimer).toHaveBeenCalled();
        expect(authService.saveUserInLocalStorage).toHaveBeenCalled();
      });
    });

    it('should return an authenticateUserFailure action on failure', () => {
      (authService.registerUser as jasmine.Spy).and.returnValue(
        throwError(mockErrorResponse)
      );

      authEffects.registerUser$.subscribe((resultAction) => {
        expect(resultAction).toEqual(
          AuthActions.authenticateUserFailure({
            authErrorMessages: mockErrorResponse.error.errors,
          })
        );
        expect(authService.registerUser).toHaveBeenCalled();
      });
    });
  });

  describe('autoUserLogin$', () => {
    beforeEach(() => {
      actions$ = new ReplaySubject(1);
      actions$.next(AuthActions.autoUserLogin());
    });

    it('should return an authenticateUserSuccess action when user data is stored in local storage', () => {
      (authService.getUserFromLocalStorage as jasmine.Spy).and.returnValue(
        of(mockUser)
      );

      authEffects.autoUserLogin$.subscribe((resultAction) => {
        expect(resultAction.type).toEqual('[User] Authenticate User Success');
        expect(authService.getUserFromLocalStorage).toHaveBeenCalled();
      });
    });

    it('should return a dummy action when user data is not stored in local storage', () => {
      authEffects.autoUserLogin$.subscribe((resultAction) => {
        expect(resultAction.type).toEqual('DUMMY');
        expect(authService.getUserFromLocalStorage).toHaveBeenCalled();
      });
    });
  });

  describe('logoutUser$', () => {
    beforeEach(() => {
      actions$ = new ReplaySubject(1);
      actions$.next(AuthActions.logoutUser());
    });

    it('should logout user and remove user data from local storage', () => {
      authEffects.logoutUser$.subscribe(() => {
        expect(authService.removeUserFromLocalStorage).toHaveBeenCalled();
      });
    });
  });
});
