import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';

import CityModule from '../city/city.module';
import { MaterialModule } from '../common/material.module';
import { CountryModule } from '../country/country.module';
import { ProvinceModule } from '../province/province.module';
import { TargetModule } from '../target/target.module';
import { VictimModule } from '../victim/victim.module';
import { EventFormWrapperComponent } from './event-form-wrapper/event-form-wrapper.component';
import { EventFormComponent } from './event-form/event-form.component';
import EventEffects from './store/event.effects';
import eventReducer from './store/event.reducer';

@NgModule({
  declarations: [EventFormComponent, EventFormWrapperComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MaterialModule,
    StoreModule.forFeature('event', eventReducer),
    EffectsModule.forFeature([EventEffects]),

    VictimModule,
    TargetModule,
    CityModule,
    ProvinceModule,
    CountryModule,
  ],
  exports: [EventFormWrapperComponent],
})
export default class EventModule {}
