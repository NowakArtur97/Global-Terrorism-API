import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { MaterialModule } from '../material/material.module';
import AuthRoutingModule from './auth-routing.module';
import { AuthenticationComponent } from './authentication/authentication.component';

@NgModule({
  declarations: [AuthenticationComponent],
  imports: [FormsModule, AuthRoutingModule, MaterialModule],
  exports: [AuthenticationComponent],
})
export default class AuthModule {}
