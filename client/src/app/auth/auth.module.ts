import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';

import { MaterialModule } from '../material/material.module';
import AuthRoutingModule from './auth-routing.module';
import { AuthenticationComponent } from './authentication/authentication.component';
import { RegistrationComponent } from './registration/registration.component';
import AuthEffects from './store/auth.effects';
import authReducer from './store/auth.reducer';

@NgModule({
  declarations: [AuthenticationComponent, RegistrationComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    AuthRoutingModule,
    MaterialModule,

    StoreModule.forFeature('auth', authReducer),
    EffectsModule.forFeature([AuthEffects]),
  ],
  exports: [AuthenticationComponent, RegistrationComponent],
})
export default class AuthModule {}
