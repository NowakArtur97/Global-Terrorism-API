import { NgModule } from '@angular/core';
import { StoreModule } from '@ngrx/store';

import { CitiesComponent } from './city.component';
import cityReducer from './store/city.reducer';

@NgModule({
  declarations: [CitiesComponent],
  imports: [StoreModule.forFeature('recipes', cityReducer)],
})
export class CitiesModule {}
