import { NgModule } from '@angular/core';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';

import CityEffects from './store/city.effects';
import cityReducer from './store/city.reducer';

@NgModule({
  declarations: [],
  imports: [
    StoreModule.forFeature('city', cityReducer),
    EffectsModule.forFeature([CityEffects]),

    LeafletModule,
  ],
  exports: [],
})
export default class CityModule {}
