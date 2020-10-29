import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';

import CityModule from '../city/city.module';
import { MaterialModule } from '../common/material.module';
import { VictimModule } from '../victim/victim.module';
import { EventFormComponent } from './event-form/event-form.component';
import EventEffects from './store/event.effects';
import eventReducer from './store/event.reducer';
import { EventFormWrapperComponent } from './event-form-wrapper/event-form-wrapper.component';

@NgModule({
  declarations: [EventFormComponent, EventFormWrapperComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MaterialModule,
    StoreModule.forFeature('event', eventReducer),
    EffectsModule.forFeature([EventEffects]),

    VictimModule,
    CityModule,
  ],
  exports: [EventFormComponent],
})
export default class EventModule {}
