import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import EventResolver from '../event/event.resolver';
import { MapComponent } from './map/map.component';

const appRoutes: Routes = [
  {
    path: 'map',
    component: MapComponent,
    resolve: [EventResolver],
  },
];

@NgModule({
  imports: [RouterModule.forChild(appRoutes)],
  exports: [RouterModule],
})
export default class MapRoutingModule {}
