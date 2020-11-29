import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { DashboardComponent } from '../chart/dashboard/dashboard.component';
import EventResolver from '../event/event.resolver';

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
