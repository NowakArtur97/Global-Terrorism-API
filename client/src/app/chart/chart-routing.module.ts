import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import EventResolver from '../event/event.resolver';
import { DashboardComponent } from './dashboard/dashboard.component';

const appRoutes: Routes = [
  {
    path: 'chart-dashboard',
    component: DashboardComponent,
    resolve: [EventResolver],
  },
];

@NgModule({
  imports: [RouterModule.forChild(appRoutes)],
  exports: [RouterModule],
})
export default class ChartRoutingModule {}
