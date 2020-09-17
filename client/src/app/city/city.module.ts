import { NgModule } from '@angular/core';
import { StoreModule } from '@ngrx/store';

import { CityComponent } from './city.component';
import cityReducer from './store/city.reducer';

@NgModule({
  declarations: [CityComponent],
  imports: [StoreModule.forFeature('recipes', cityReducer)],
})
export class CititesModule {}
