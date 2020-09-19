import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { MaterialModule } from '../material/material.module';
import AuthRoutingModule from './auth-routing.module';
import { AuthComponent } from './auth.component';
import { AuthenticationComponent } from './authentication/authentication.component';

@NgModule({
  declarations: [AuthComponent, AuthenticationComponent],
  imports: [FormsModule, AuthRoutingModule, MaterialModule],
  exports: [],
})
export default class AuthModule {}
