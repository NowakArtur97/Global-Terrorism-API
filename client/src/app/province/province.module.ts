import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { ProvinceFormComponent } from './province-form/province-form.component';

@NgModule({
  declarations: [ProvinceFormComponent],
  imports: [CommonModule],
  exports: [ProvinceFormComponent],
})
export class ProvinceModule {}
