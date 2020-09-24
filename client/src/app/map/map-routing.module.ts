import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import AuthGuard from '../auth/auth.guard';
import { CitiesResolver } from '../cities/cities.resolver';
import { MapComponent } from './map.component';

const appRoutes: Routes = [
  {
    path: 'map',
    component: MapComponent,
    canActivate: [AuthGuard],
    resolve: [CitiesResolver],
  },
];

@NgModule({
  imports: [RouterModule.forChild(appRoutes)],
  exports: [RouterModule],
})
export default class MapRoutingModule {}
