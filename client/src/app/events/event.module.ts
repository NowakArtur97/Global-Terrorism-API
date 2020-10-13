import { NgModule } from '@angular/core';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';

import EventEffects from './store/event.effects';
import eventReducer from './store/event.reducer';

@NgModule({
  declarations: [],
  imports: [
    StoreModule.forFeature('event', eventReducer),
    EffectsModule.forFeature([EventEffects]),
  ],
  exports: [],
})
export default class EventModule {}
