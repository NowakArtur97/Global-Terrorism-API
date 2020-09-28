import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Store, StoreModule } from '@ngrx/store';
import { of } from 'rxjs';
import AppStoreState from 'src/app/store/app.store.state';

import RegistrationData from '../models/RegistrationData';
import * as AuthActions from '../store/auth.actions';
import { RegistrationComponent } from './registration.component';

describe('RegistrationComponent', () => {
  let component: RegistrationComponent;
  let fixture: ComponentFixture<RegistrationComponent>;
  let store: Store<AppStoreState>;

  const registrationData = new RegistrationData(
    'username',
    'email@email.com',
    'password',
    'password'
  );

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StoreModule.forRoot({}), ReactiveFormsModule],
      declarations: [RegistrationComponent],
      providers: [Store],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RegistrationComponent);
    component = fixture.componentInstance;

    store = TestBed.inject(Store);
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

    it('with empty username should be invalid', () => {
      const userName = component.registerForm.controls.userName;

      userName.setValue('');

      const errors = userName.errors;
      expect(errors.required).toBeTruthy();
    });

    it('with empty email should be invalid', () => {
      const email = component.registerForm.controls.email;

      email.setValue('');

      const errors = email.errors;
      expect(errors.required).toBeTruthy();
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

      const errors = password.errors;
      expect(errors.required).toBeTruthy();
    });

    it('with empty matching password should be invalid', () => {
      const matchingPassword = component.registerForm.controls.matchingPassword;

      matchingPassword.setValue('');

      const errors = matchingPassword.errors;
      expect(errors.required).toBeTruthy();
    });
  });

  describe('when register form is submitted', () => {
    it('should be invalid by default', () => {
      expect(component.registerForm.valid).toBeFalsy();
    });

    it('should dispatch registerUserStart action when register form is valid', () => {
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

      expect(component.registerForm.valid).toBeTruthy();

      component.onRegister();

      expect(store.select).toHaveBeenCalled();
      expect(store.dispatch).toHaveBeenCalledWith(
        AuthActions.registerUserStart({ registrationData })
      );
    });
  });
});
