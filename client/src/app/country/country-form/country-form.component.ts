import { Component, forwardRef } from '@angular/core';
import { FormControl, FormGroup, NG_VALIDATORS, NG_VALUE_ACCESSOR, Validators } from '@angular/forms';
import { AbstractFormComponent } from 'src/app/common/components/abstract-form.component';

@Component({
  selector: 'app-country-form',
  templateUrl: './country-form.component.html',
  styleUrls: ['./country-form.component.css'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => CountryFormComponent),
      multi: true,
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => CountryFormComponent),
      multi: true,
    },
  ],
})
export class CountryFormComponent extends AbstractFormComponent {
  initForm(): void {
    this.formGroup = new FormGroup({
      name: new FormControl('', Validators.required),
    });
  }
}
