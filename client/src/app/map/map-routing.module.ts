import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import CityResolver from '../cities/city.resolver';
import { MapComponent } from './map.component';

const appRoutes: Routes = [
  {
    path: 'map',
    component: MapComponent,
    resolve: [CityResolver],
  },
];

@NgModule({
  imports: [RouterModule.forChild(appRoutes)],
  exports: [RouterModule],
})
export default class MapRoutingModule {}
