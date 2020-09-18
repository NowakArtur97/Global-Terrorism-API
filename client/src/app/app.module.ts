import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { environment } from 'src/environments/environment';

import { AppComponent } from './app.component';
import CitiesModule from './cities/cities.module';
import CitiesEffects from './cities/store/cities.effects';
import { MaterialModule } from './material/material.module';
import appReducer from './store/app.reducer';

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    StoreModule.forRoot(appReducer),
    StoreDevtoolsModule.instrument({ logOnly: environment.production }),
    EffectsModule.forRoot([CitiesEffects]),

    BrowserAnimationsModule,
    MaterialModule,

    CitiesModule,
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
