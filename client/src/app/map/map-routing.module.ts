import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import CityResolver from '../city/city.resolver';
import EventResolver from '../event/event.resolver';
import { MapComponent } from './map/map.component';

const appRoutes: Routes = [
  {
    path: 'map',
    component: MapComponent,
    resolve: [CityResolver, EventResolver],
  },
];

@NgModule({
  imports: [RouterModule.forChild(appRoutes)],
  exports: [RouterModule],
})
export default class MapRoutingModule {}
