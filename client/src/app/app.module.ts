import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { CititesModule } from './city/city.module';

@NgModule({
  declarations: [AppComponent],
  imports: [BrowserModule, CititesModule],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
