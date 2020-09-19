import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AuthComponent } from './auth.component';
import { AuthenticationComponent } from './authentication/authentication.component';

const appRoutes: Routes = [
  {
    path: 'auth',
    component: AuthComponent,
    pathMatch: 'full',
    children: [{ path: 'authentication', component: AuthenticationComponent }],
  },
];

@NgModule({
  imports: [RouterModule.forChild(appRoutes)],
  exports: [RouterModule],
})
export default class AuthRoutingModule {}
