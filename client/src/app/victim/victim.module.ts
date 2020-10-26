import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { MaterialModule } from '../common/material.module';
import { VictimFormComponent } from './victim-form/victim-form.component';

@NgModule({
  declarations: [VictimFormComponent],
  imports: [CommonModule, ReactiveFormsModule, MaterialModule],
  exports: [VictimFormComponent],
})
export class VictimModule {}
