import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Store, StoreModule } from '@ngrx/store';
import { of } from 'rxjs';
import AppStoreState from 'src/app/store/app.store.state';

import LoginData from '../models/LoginData';
import * as AuthActions from '../store/auth.actions';
import { AuthenticationComponent } from './authentication.component';

describe('AuthenticationComponent', () => {
  let component: AuthenticationComponent;
  let fixture: ComponentFixture<AuthenticationComponent>;
  let store: Store<AppStoreState>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StoreModule.forRoot({}), ReactiveFormsModule],
      declarations: [AuthenticationComponent],
      providers: [Store],
    }).compileComponents();

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

  describe('when login form is submitted', () => {
    it('form invalid when empty', () => {
      expect(component.loginForm.valid).toBeFalsy();
    });

    it('should dispatch loginUserStart action when login form is valid', () => {
      expect(component.loginForm.valid).toBeFalsy();

      const loginData = new LoginData('login', ' password');

      component.loginForm.controls['userNameOrEmail'].setValue(
        loginData.userNameOrEmail
      );
      component.loginForm.controls['password'].setValue(loginData.password);

      expect(component.loginForm.valid).toBeTruthy();

      component.onLogin();

      expect(store.dispatch).toHaveBeenCalledWith(
        AuthActions.loginUserStart({ loginData })
      );
    });
  });
});
