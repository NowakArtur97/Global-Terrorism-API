import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import AuthRoutingModule from './auth-routing.module';
import { AuthComponent } from './auth.component';

@NgModule({
  declarations: [AuthComponent],
  imports: [FormsModule, AuthRoutingModule],
  exports: [],
})
export default class AuthModule {}
