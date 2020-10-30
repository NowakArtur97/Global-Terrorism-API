import { Component, forwardRef } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, NG_VALIDATORS, NG_VALUE_ACCESSOR, Validators } from '@angular/forms';
import { AbstractFormComponent } from 'src/app/common/components/abstract-form.component';
import CommonValidators from 'src/app/common/validators/common.validator';

@Component({
  selector: 'app-city-form',
  templateUrl: './city-form.component.html',
  styleUrls: ['./city-form.component.css'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => CityFormComponent),
      multi: true,
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => CityFormComponent),
      multi: true,
    },
  ],
})
export class CityFormComponent extends AbstractFormComponent {
  initForm(): void {
    this.formGroup = new FormGroup({
      name: new FormControl('', [CommonValidators.notBlank]),
      latitude: new FormControl('', [
        Validators.min(-90),
        Validators.max(90),
        CommonValidators.notBlank,
      ]),
      longitude: new FormControl('', [
        Validators.min(-180),
        Validators.max(180),
        CommonValidators.notBlank,
      ]),
    });
  }

  get name(): AbstractControl {
    return this.formGroup.get('name');
  }

  get latitude(): AbstractControl {
    return this.formGroup.get('latitude');
  }

  get longitude(): AbstractControl {
    return this.formGroup.get('longitude');
  }
}
