import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, fakeAsync, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Store, StoreModule } from '@ngrx/store';
import { of } from 'rxjs';
import AppStoreState from 'src/app/store/app.store.state';

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

  const registrationData = new RegistrationData(
    'UserName',
    'email@email.com',
    'Password123!',
    'Password123!'
  );

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        StoreModule.forRoot({}),
        HttpClientTestingModule,
        ReactiveFormsModule,
      ],
      declarations: [RegistrationComponent],
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
    spyOn(authService, 'checkUserData').and.callThrough();

    fixture.detectChanges();
    component.ngOnInit();
  });

  describe('when initialize component', () => {
    it('should select error messages from store', () => {
      expect(store.select).toHaveBeenCalled();
    });
  });

  describe('form valdiation', () => {
    beforeEach(() => {
      component.registerForm.controls.userName.setValue(
        registrationData.userName
      );
      component.registerForm.controls.email.setValue(registrationData.email);
      component.registerForm.controls.password.setValue(
        registrationData.password
      );
      component.registerForm.controls.matchingPassword.setValue(
        registrationData.matchingPassword
      );
    });

    it('with empty user name should be invalid', () => {
      const userName = component.registerForm.controls.userName;
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
      const userName = component.registerForm.controls.userName;
      userName.setValue('1');
      const errors = userName.errors;

      expect(errors.minlength).toBeTruthy();
    });

    it('with too long matching password should be invalid', () => {
      const userName = component.registerForm.controls.userName;
      userName.setValue('1234567890123456789012345678901');
      const errors = userName.errors;

      expect(errors.maxlength).toBeTruthy();
    });

    it('with empty email should be invalid', () => {
      const email = component.registerForm.controls.email;
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
      const email = component.registerForm.controls.email;
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

    it('with empty password should be invalid', () => {
      const password = component.registerForm.controls.password;
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
      const password = component.registerForm.controls.password;
      password.setValue('1');
      const errors = password.errors;

      expect(errors.minlength).toBeTruthy();
    });

    it('with too long password should be invalid', () => {
      const password = component.registerForm.controls.password;
      password.setValue('1234567890123456789012345678901');
      const errors = password.errors;

      expect(errors.maxlength).toBeTruthy();
    });

    it('with password with spaces should be invalid', () => {
      const password = component.registerForm.controls.password;
      password.setValue('pass word');
      const errors = password.errors;

      expect(errors.withoutSpaces).toBeTruthy();
    });

    it('with password with multiple repetetive characters should be invalid', () => {
      const password = component.registerForm.controls.password;
      password.setValue('pAAAsword!');
      const errors = password.errors;

      expect(errors.repetitiveCharacters).toBeTruthy();
    });

    it('with popular password should be invalid', () => {
      const password = component.registerForm.controls.password;
      password.setValue('iloveyou');
      const errors = password.errors;

      expect(errors.notPopular).toBeTruthy();
    });

    it('with empty matching password should be invalid', () => {
      const matchingPassword = component.registerForm.controls.matchingPassword;
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
      const matchingPassword = component.registerForm.controls.matchingPassword;
      matchingPassword.setValue('1');
      const errors = matchingPassword.errors;

      expect(errors.minlength).toBeTruthy();
    });

    it('with too long matching password should be invalid', () => {
      const matchingPassword = component.registerForm.controls.matchingPassword;
      matchingPassword.setValue('1234567890123456789012345678901');
      const errors = matchingPassword.errors;

      expect(errors.maxlength).toBeTruthy();
    });

    it('with matching password with spaces should be invalid', () => {
      const matchingPassword = component.registerForm.controls.matchingPassword;
      matchingPassword.setValue('pass word');
      const errors = matchingPassword.errors;

      expect(errors.withoutSpaces).toBeTruthy();
    });

    it('with matching password with multiple repetetive characters should be invalid', () => {
      const matchingPassword = component.registerForm.controls.matchingPassword;
      matchingPassword.setValue('pAAAsword!');
      const errors = matchingPassword.errors;

      expect(errors.repetitiveCharacters).toBeTruthy();
    });

    it('with popular matching password should be invalid', () => {
      const matchingPassword = component.registerForm.controls.matchingPassword;
      matchingPassword.setValue('iloveyou');
      const errors = matchingPassword.errors;

      expect(errors.notPopular).toBeTruthy();
    });
  });

  describe('when register form is submitted', () => {
    it('should be invalid by default', () => {
      expect(component.registerForm.valid).toBeFalsy();
    });

    it('should dispatch registerUserStart action when register form is valid', fakeAsync(() => {
      expect(component.registerForm.valid).toBeFalsy();

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

      // expect(component.registerForm.valid).toBeTruthy();

      component.onRegister();

      expect(store.select).toHaveBeenCalled();
      expect(store.dispatch).toHaveBeenCalledWith(
        AuthActions.registerUserStart({ registrationData })
      );
    }));
  });
});
