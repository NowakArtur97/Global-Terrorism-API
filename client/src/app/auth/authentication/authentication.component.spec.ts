import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Store, StoreModule } from '@ngrx/store';
import { of } from 'rxjs';
import AppStoreState from 'src/app/store/app.state';

import LoginData from '../models/login-data.model';
import * as AuthActions from '../store/auth.actions';
import { AuthenticationComponent } from './authentication.component';

describe('AuthenticationComponent', () => {
  let component: AuthenticationComponent;
  let fixture: ComponentFixture<AuthenticationComponent>;
  let store: Store<AppStoreState>;
  const loginData = new LoginData('login', ' password');

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AuthenticationComponent],
      imports: [StoreModule.forRoot({}), ReactiveFormsModule],
      providers: [Store],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AuthenticationComponent);
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

  describe('form validation', () => {
    beforeEach(() => {
      component.userNameOrEmail.setValue(loginData.userNameOrEmail);
      component.password.setValue(loginData.password);
    });

    it('with empty username/email should be invalid', () => {
      component.userNameOrEmail.setValue('');

      const userNameOrEmail = component.userNameOrEmail;
      const errors = userNameOrEmail.errors;
      expect(errors.required).toBeTruthy();
    });

    it('with empty password should be invalid', () => {
      component.password.setValue('');

      const password = component.password;
      const errors = password.errors;
      expect(errors.required).toBeTruthy();
    });
  });

  describe('when login form is submitted', () => {
    it('form invalid when empty', () => {
      expect(component.loginForm.valid).toBeFalsy();
    });

    it('should dispatch loginUserStart action when login form is valid', () => {
      expect(component.loginForm.valid).toBeFalsy();

      component.userNameOrEmail.setValue(loginData.userNameOrEmail);
      component.password.setValue(loginData.password);

      expect(component.loginForm.valid).toBeTruthy();

      component.onLogin();

      expect(store.dispatch).toHaveBeenCalledWith(
        AuthActions.loginUserStart({ loginData })
      );
    });
  });
});
