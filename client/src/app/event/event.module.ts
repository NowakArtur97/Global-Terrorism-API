import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';

import { MaterialModule } from '../common/material.module';
import { EventFormComponent } from './event-form/event-form.component';
import EventEffects from './store/event.effects';
import eventReducer from './store/event.reducer';

@NgModule({
  declarations: [EventFormComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MaterialModule,
    StoreModule.forFeature('event', eventReducer),
    EffectsModule.forFeature([EventEffects]),
  ],
  exports: [EventFormComponent],
})
export default class EventModule {}
