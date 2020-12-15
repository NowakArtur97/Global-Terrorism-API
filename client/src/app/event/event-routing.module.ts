import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import AuthGuard from '../auth/auth.guard';
import { EventListComponent } from './event-list/event-list.component';
import EventResolver from './event.resolver';

const appRoutes: Routes = [
  {
    path: 'event-list',
    component: EventListComponent,
    resolve: [EventResolver],
    canActivate: [AuthGuard],
  },
];

@NgModule({
  imports: [RouterModule.forChild(appRoutes)],
  exports: [RouterModule],
})
export default class EventRoutingModule {}
