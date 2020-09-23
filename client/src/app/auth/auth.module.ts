import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { MaterialModule } from '../material/material.module';
import AuthRoutingModule from './auth-routing.module';
import { AuthenticationComponent } from './authentication/authentication.component';
import { RegistrationComponent } from './registration/registration.component';

@NgModule({
  declarations: [AuthenticationComponent, RegistrationComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    AuthRoutingModule,
    MaterialModule,
  ],
  exports: [AuthenticationComponent, RegistrationComponent],
})
export default class AuthModule {}
