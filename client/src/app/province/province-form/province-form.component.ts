import { Component, forwardRef } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, NG_VALIDATORS, NG_VALUE_ACCESSOR } from '@angular/forms';
import { AbstractFormComponent } from 'src/app/common/components/abstract-form.component';
import CommonValidators from 'src/app/common/validators/common.validator';
import { selectEventToUpdate } from 'src/app/event/store/event.reducer';

import Event from '../../event/models//event.model';

@Component({
  selector: 'app-province-form',
  templateUrl: './province-form.component.html',
  styleUrls: ['./province-form.component.css'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => ProvinceFormComponent),
      multi: true,
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => ProvinceFormComponent),
      multi: true,
    },
  ],
})
export class ProvinceFormComponent extends AbstractFormComponent {
  initForm(): void {
    let name = '';

    this.store.select(selectEventToUpdate).subscribe((event: Event) => {
      if (event?.city?.province) {
        const province = event.city.province;
        name = province.name;
      }
    });

    this.formGroup = new FormGroup({
      name: new FormControl('', [CommonValidators.notBlank]),
    });
  }

  get name(): AbstractControl {
    return this.formGroup.get('name');
  }
}
