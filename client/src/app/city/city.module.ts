import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';

import { MaterialModule } from '../common/material.module';
import { CityFormComponent } from './city-form/city-form.component';
import CityEffects from './store/city.effects';
import cityReducer from './store/city.reducer';

@NgModule({
  declarations: [CityFormComponent],
  imports: [
    CommonModule,
    StoreModule.forFeature('city', cityReducer),
    EffectsModule.forFeature([CityEffects]),

    LeafletModule,
    ReactiveFormsModule,

    MaterialModule,
  ],
  exports: [CityFormComponent],
})
export class CityModule {}
