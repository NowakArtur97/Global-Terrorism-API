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

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StoreModule.forRoot({}), ReactiveFormsModule],
      declarations: [RegistrationComponent],
      providers: [Store],
    }).compileComponents();

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

  describe('when register form is submitted', () => {
    it('form invalid when empty', () => {
      expect(component.registerForm.valid).toBeFalsy();
    });

    it('should dispatch registerUserStart action when register form is valid', () => {
      expect(component.registerForm.valid).toBeFalsy();

      const registrationData = new RegistrationData(
        'username',
        'email@email.com',
        'password',
        'password'
      );

      component.registerForm.controls['userName'].setValue(
        registrationData.userName
      );
      component.registerForm.controls['email'].setValue(registrationData.email);
      component.registerForm.controls['password'].setValue(
        registrationData.password
      );
      component.registerForm.controls['matchingPassword'].setValue(
        registrationData.matchingPassword
      );

      expect(component.registerForm.valid).toBeTruthy();

      component.onRegister();

      expect(store.dispatch).toHaveBeenCalledWith(
        AuthActions.registerUserStart({ registrationData })
      );
    });
  });
});
