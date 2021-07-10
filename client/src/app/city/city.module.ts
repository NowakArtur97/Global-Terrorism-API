import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';

import { MaterialModule } from '../common/material.module';
import { CityFormComponent } from './city-form/city-form.component';

@NgModule({
  declarations: [CityFormComponent],
  imports: [CommonModule, LeafletModule, ReactiveFormsModule, MaterialModule],
  exports: [CityFormComponent],
})
export class CityModule {}
