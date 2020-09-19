import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AuthComponent } from './auth.component';

const appRoutes: Routes = [
  {
    path: 'auth',
    component: AuthComponent,
    pathMatch: 'full',
  },
];

@NgModule({
  imports: [RouterModule.forChild(appRoutes)],
  exports: [RouterModule],
})
export default class AuthRoutingModule {}
