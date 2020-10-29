import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { MaterialModule } from '../common/material.module';
import { ProvinceFormComponent } from './province-form/province-form.component';

@NgModule({
  declarations: [ProvinceFormComponent],
  imports: [CommonModule, ReactiveFormsModule, MaterialModule],
  exports: [ProvinceFormComponent],
})
export class ProvinceModule {}
