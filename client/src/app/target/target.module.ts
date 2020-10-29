import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { MaterialModule } from '../common/material.module';
import { TargetFormComponent } from './target-form/target-form.component';

@NgModule({
  declarations: [TargetFormComponent],
  imports: [CommonModule, ReactiveFormsModule, MaterialModule],
  exports: [TargetFormComponent],
})
export class TargetModule {}
