import { Component, forwardRef } from '@angular/core';
import { FormControl, FormGroup, NG_VALIDATORS, NG_VALUE_ACCESSOR, Validators } from '@angular/forms';
import { AbstractFormComponent } from 'src/app/common/components/abstract-form.component';

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
      name: new FormControl('', Validators.required),
      latitude: new FormControl('', Validators.required),
      longitude: new FormControl('', Validators.required),
    });
  }
}
