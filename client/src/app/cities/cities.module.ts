import { NgModule } from '@angular/core';
import { StoreModule } from '@ngrx/store';

import { CitiesComponent } from './city.component';
import citiesReducer from './store/cities.reducer';

@NgModule({
  declarations: [CitiesComponent],
  imports: [StoreModule.forFeature('recipes', citiesReducer)],
})
export class CitiesModule {}
