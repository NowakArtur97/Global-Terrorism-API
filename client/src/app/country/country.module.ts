import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { MaterialModule } from '../common/material.module';
import { CountryFormComponent } from './country-form/country-form.component';

@NgModule({
  declarations: [CountryFormComponent],
  imports: [CommonModule, ReactiveFormsModule, MaterialModule],
  exports: [CountryFormComponent],
})
export class CountryModule {}
