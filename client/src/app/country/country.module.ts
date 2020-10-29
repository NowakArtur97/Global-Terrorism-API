import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { CountryFormComponent } from './country-form/country-form.component';

@NgModule({
  declarations: [CountryFormComponent],
  imports: [CommonModule],
  exports: [CountryFormComponent],
})
export class CountryModule {}
