import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Store, StoreModule } from '@ngrx/store';
import { of } from 'rxjs';
import { MaterialModule } from 'src/app/common/material.module';
import AppStoreState from 'src/app/store/app.state';

import RegistrationData from '../models/registration-data.model';
import AuthService from '../services/auth.service';
import * as AuthActions from '../store/auth.actions';
import { RegistrationComponent } from './registration.component';
import UserDataValidators from './validators/user-data.validator';

describe('RegistrationComponent', () => {
  let component: RegistrationComponent;
  let fixture: ComponentFixture<RegistrationComponent>;
  let store: Store<AppStoreState>;
  let authService: AuthService;

  const registrationData: RegistrationData = {
    userName: 'UserName',
    email: 'email@email.com',
    password: 'Password123!',
    matchingPassword: 'Password123!',
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegistrationComponent],
      imports: [
        StoreModule.forRoot({}),
        HttpClientTestingModule,
        ReactiveFormsModule,
        MaterialModule,
        BrowserAnimationsModule,
      ],
      providers: [Store, AuthService, UserDataValidators],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RegistrationComponent);
    component = fixture.componentInstance;

    store = TestBed.inject(Store);
    authService = TestBed.inject(AuthService);

    spyOn(store, 'select').and.callFake((selector) => {
      if (selector === 'auth') {
        return of([]);
      }
    });
    spyOn(store, 'dispatch');

    fixture.detectChanges();
    component.ngOnInit();
  });

  describe('when initialize component', () => {
    it('should select error messages from store', () => {
      expect(store.select).toHaveBeenCalled();
    });
  });

  describe('form validation', () => {
    beforeEach(() => {
      component.userName.setValue(registrationData.userName);
      component.email.setValue(registrationData.email);
      component.password.setValue(registrationData.password);
      component.matchingPassword.setValue(registrationData.matchingPassword);
    });

    it('with empty user name should be invalid', () => {
      const userName = component.userName;
      userName.setValue('');
      let errors = userName.errors;

      expect(errors.notBlank).toBeTruthy();

      userName.setValue(' ');
      errors = userName.errors;

      expect(errors.notBlank).toBeTruthy();

      userName.setValue('    ');
      errors = userName.errors;

      expect(errors.notBlank).toBeTruthy();
    });

    it('with too short user name should be invalid', () => {
      const userName = component.userName;
      userName.setValue('1');
      const errors = userName.errors;

      expect(errors.minlength).toBeTruthy();
    });

    it('with too long user name should be invalid', () => {
      const userName = component.userName;
      userName.setValue('1234567890123456789012345678901');
      const errors = userName.errors;

      expect(errors.maxlength).toBeTruthy();
    });

    it('with user name already taken should be invalid', () => {
      spyOn(authService, 'checkUserData').and.callFake(() =>
        of({ isUserNameAvailable: false, isEmailAvailable: true })
      );

      const userName = component.userName;
      userName.setValue('usernameTaken');
      const errors = userName.errors;

      expect(errors.userNameAlreadyTaken).toBeTruthy();
    });

    it('with empty email should be invalid', () => {
      const email = component.email;
      email.setValue('');
      let errors = email.errors;

      expect(errors.notBlank).toBeTruthy();

      email.setValue(' ');
      errors = email.errors;

      expect(errors.notBlank).toBeTruthy();

      email.setValue('    ');
      errors = email.errors;

      expect(errors.notBlank).toBeTruthy();
    });

    it('with incorrect email format should be invalid', () => {
      const email = component.email;
      email.setValue('wrongemailformat');
      let errors = email.errors;

      expect(errors.email).toBeTruthy();

      email.setValue('wrong@emailformat.');
      errors = email.errors;

      expect(errors.email).toBeTruthy();

      email.setValue('wrongemailformat.com');
      errors = email.errors;

      expect(errors.email).toBeTruthy();
    });

    it('with email already taken should be invalid', () => {
      spyOn(authService, 'checkUserData').and.callFake(() =>
        of({ isUserNameAvailable: true, isEmailAvailable: false })
      );

      const email = component.email;
      email.setValue('emailTaken@email.com');
      const errors = email.errors;

      expect(errors.emailAlreadyTaken).toBeTruthy();
    });

    it('with empty password should be invalid', () => {
      const password = component.password;
      password.setValue('');
      let errors = password.errors;

      expect(errors.notBlank).toBeTruthy();

      password.setValue(' ');
      errors = password.errors;

      expect(errors.notBlank).toBeTruthy();

      password.setValue('    ');
      errors = password.errors;

      expect(errors.notBlank).toBeTruthy();
    });

    it('with too short password should be invalid', () => {
      const password = component.password;
      password.setValue('1');
      const errors = password.errors;

      expect(errors.minlength).toBeTruthy();
    });

    it('with too long password should be invalid', () => {
      const password = component.password;
      password.setValue('1234567890123456789012345678901');
      const errors = password.errors;

      expect(errors.maxlength).toBeTruthy();
    });

    it('with password with spaces should be invalid', () => {
      const password = component.password;
      password.setValue('pass word');
      const errors = password.errors;

      expect(errors.withoutSpaces).toBeTruthy();
    });

    it('with password with multiple repetetive characters should be invalid', () => {
      const password = component.password;
      password.setValue('pAAAsword!');
      const errors = password.errors;

      expect(errors.repetitiveCharacters).toBeTruthy();
    });

    it('with popular password should be invalid', () => {
      const password = component.password;
      password.setValue('iloveyou');
      const errors = password.errors;

      expect(errors.notPopular).toBeTruthy();
    });

    it('with password including user name should be invalid', () => {
      const userName = component.userName;
      const password = component.password;
      userName.setValue('userName123');
      password.setValue('someTextuserName123123');
      const errors = password.errors;

      expect(errors.notInclude).toBeTruthy();
    });

    it('with empty matching password should be invalid', () => {
      const matchingPassword = component.matchingPassword;
      matchingPassword.setValue('');
      let errors = matchingPassword.errors;

      expect(errors.notBlank).toBeTruthy();

      matchingPassword.setValue(' ');
      errors = matchingPassword.errors;

      expect(errors.notBlank).toBeTruthy();

      matchingPassword.setValue('    ');
      errors = matchingPassword.errors;

      expect(errors.notBlank).toBeTruthy();
    });

    it('with too short matching password should be invalid', () => {
      const matchingPassword = component.matchingPassword;
      matchingPassword.setValue('1');
      const errors = matchingPassword.errors;

      expect(errors.minlength).toBeTruthy();
    });

    it('with too long matching password should be invalid', () => {
      const matchingPassword = component.matchingPassword;
      matchingPassword.setValue('1234567890123456789012345678901');
      const errors = matchingPassword.errors;

      expect(errors.maxlength).toBeTruthy();
    });

    it('with matching password with spaces should be invalid', () => {
      const matchingPassword = component.matchingPassword;
      matchingPassword.setValue('pass word');
      const errors = matchingPassword.errors;

      expect(errors.withoutSpaces).toBeTruthy();
    });

    it('with matching password with multiple repetetive characters should be invalid', () => {
      const matchingPassword = component.matchingPassword;
      matchingPassword.setValue('pAAAsword!');
      const errors = matchingPassword.errors;

      expect(errors.repetitiveCharacters).toBeTruthy();
    });

    it('with popular matching password should be invalid', () => {
      const matchingPassword = component.matchingPassword;
      matchingPassword.setValue('iloveyou');
      const errors = matchingPassword.errors;

      expect(errors.notPopular).toBeTruthy();
    });

    it('with matching password including user name should be invalid', () => {
      const userName = component.userName;
      const matchingPassword = component.matchingPassword;
      userName.setValue('userName123');
      matchingPassword.setValue('someTextuserName123123');
      const errors = matchingPassword.errors;

      expect(errors.notInclude).toBeTruthy();
    });

    it('with passwords not meeting any characteristic rules should be invalid', () => {
      const password = component.password;
      const matchingPassword = component.matchingPassword;
      password.setValue('');
      matchingPassword.setValue('');
      const errors = password.errors;

      expect(errors.withoutUppercase).toBeTruthy();
      expect(errors.withoutLowercase).toBeTruthy();
      expect(errors.withoutDigits).toBeTruthy();
      expect(errors.withoutSpecial).toBeTruthy();
    });

    it('with passwords meeting only one characteristic rules should be invalid', () => {
      const password = component.password;
      const matchingPassword = component.matchingPassword;
      password.setValue('A');
      matchingPassword.setValue('A');
      const errors = password.errors;

      expect(errors.withoutLowercase).toBeTruthy();
      expect(errors.withoutDigits).toBeTruthy();
      expect(errors.withoutSpecial).toBeTruthy();
    });

    it('with passwords meeting two characteristic rules should be valid', () => {
      spyOn(authService, 'checkUserData').and.callFake(() =>
        of({ isUserNameAvailable: true, isEmailAvailable: true })
      );

      const password = component.password;
      const matchingPassword = component.matchingPassword;
      password.setValue('Password');
      matchingPassword.setValue('Password');
      const errors = password.errors;

      expect(errors).toBeNull();
      expect(component.registerForm.valid).toBeTruthy();
    });

    it('with passwords not matching should be invalid', () => {
      const password = component.password;
      const matchingPassword = component.matchingPassword;
      password.setValue('PASSWORD!@#1');
      matchingPassword.setValue('password!@#1');
      const errors = password.errors;

      expect(errors.notMatch).toBeTruthy();
    });
  });

  describe('when register form is submitted', () => {
    it('should be invalid by default', () => {
      expect(component.registerForm.valid).toBeFalsy();
    });

    it('should dispatch registerUserStart action when register form is valid', () => {
      expect(component.registerForm.valid).toBeFalsy();

      spyOn(authService, 'checkUserData').and.callFake(() =>
        of({ isUserNameAvailable: true, isEmailAvailable: true })
      );

      const {
        userName,
        email,
        password,
        matchingPassword,
      } = component.registerForm.controls;
      userName.setValue(registrationData.userName);
      email.setValue(registrationData.email);
      password.setValue(registrationData.password);
      matchingPassword.setValue(registrationData.matchingPassword);

      expect(component.registerForm.valid).toBeTruthy();

      component.onRegister();

      expect(authService.checkUserData).toHaveBeenCalled();
      expect(store.select).toHaveBeenCalled();
      expect(store.dispatch).toHaveBeenCalledWith(
        AuthActions.registerUserStart({
          registrationData,
        })
      );
    });
  });
});
