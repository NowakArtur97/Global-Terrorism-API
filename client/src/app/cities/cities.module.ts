import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';
import { StoreModule } from '@ngrx/store';

import { CitiesComponent } from './cities.component';
import citiesReducer from './store/cities.reducer';
import { CitiesMapComponent } from './cities-map/cities-map.component';

@NgModule({
  declarations: [CitiesComponent, CitiesMapComponent],
  imports: [
    HttpClientModule,
    StoreModule.forFeature('cities', citiesReducer),
    LeafletModule,
  ],
  exports: [CitiesComponent],
})
export default class CitiesModule {}
