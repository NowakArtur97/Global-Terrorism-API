import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { StoreModule } from '@ngrx/store';

import { CitiesComponent } from './cities.component';
import citiesReducer from './store/cities.reducer';

@NgModule({
  declarations: [CitiesComponent],
  imports: [HttpClientModule, StoreModule.forFeature('cities', citiesReducer)],
  exports: [CitiesComponent],
})
export default class CitiesModule {}
