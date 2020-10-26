import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';

import { MaterialModule } from '../common/material.module';
import EventModule from '../event/event.module';
import { NavigationComponent } from './navigation/navigation.component';

@NgModule({
  declarations: [NavigationComponent],
  imports: [
    CommonModule,
    RouterModule,
    BrowserModule,
    MaterialModule,
    EventModule,
  ],
  exports: [NavigationComponent],
})
export class SharedModule {}
