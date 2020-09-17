import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { StoreModule } from '@ngrx/store';

import { AppComponent } from './app.component';
import CitiesModule from './cities/cities.module';
import appReducer from './store/app.reducer';

@NgModule({
  declarations: [AppComponent],
  imports: [BrowserModule, StoreModule.forRoot(appReducer), CitiesModule],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
