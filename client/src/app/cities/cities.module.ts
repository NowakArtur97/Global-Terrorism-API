import { NgModule } from '@angular/core';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';

import { CitiesComponent } from './cities.component';
import CitiesEffects from './store/cities.effects';
import citiesReducer from './store/cities.reducer';

@NgModule({
  declarations: [CitiesComponent],
  imports: [
    StoreModule.forFeature('cities', citiesReducer),
    EffectsModule.forFeature([CitiesEffects]),

    LeafletModule,
  ],
  exports: [CitiesComponent],
})
export default class CitiesModule {}
