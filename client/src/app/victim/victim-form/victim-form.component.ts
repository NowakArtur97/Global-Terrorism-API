import { Component, forwardRef } from '@angular/core';
import { FormControl, FormGroup, NG_VALIDATORS, NG_VALUE_ACCESSOR, Validators } from '@angular/forms';
import { AbstractForm } from 'src/app/common/components/abstract-form.component';

@Component({
  selector: 'app-victim-form',
  templateUrl: './victim-form.component.html',
  styleUrls: ['./victim-form.component.css'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => VictimFormComponent),
      multi: true,
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => VictimFormComponent),
      multi: true,
    },
  ],
})
export class VictimFormComponent extends AbstractForm {
  initForm(): void {
    this.formGroup = new FormGroup({
      totalNumberOfFatalities: new FormControl('', Validators.required),
      numberOfPerpetratorFatalities: new FormControl('', Validators.required),
      totalNumberOfInjured: new FormControl('', Validators.required),
      numberOfPerpetratorInjured: new FormControl('', Validators.required),
      valueOfPropertyDamage: new FormControl('', Validators.required),
    });
  }
}
