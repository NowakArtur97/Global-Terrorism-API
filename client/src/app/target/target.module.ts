import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { TargetFormComponent } from './target-form/target-form.component';

@NgModule({
  declarations: [TargetFormComponent],
  imports: [CommonModule],
  exports: [TargetFormComponent],
})
export class TargetModule {}
