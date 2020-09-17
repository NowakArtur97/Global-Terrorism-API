import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { CitiesModule } from './cities/cities.module';

@NgModule({
  declarations: [AppComponent],
  imports: [BrowserModule, CitiesModule],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
