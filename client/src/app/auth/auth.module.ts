import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { MaterialModule } from '../material/material.module';
import AuthRoutingModule from './auth-routing.module';
import { AuthenticationComponent } from './authentication/authentication.component';

@NgModule({
  declarations: [AuthenticationComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    AuthRoutingModule,
    MaterialModule,
  ],
  exports: [AuthenticationComponent],
})
export default class AuthModule {}
