import { Component, forwardRef, OnInit } from '@angular/core';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  ValidationErrors,
  Validators,
} from '@angular/forms';
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
export class VictimFormComponent extends AbstractForm implements OnInit {
  ngOnInit(): void {
    this.initForm();
  }

  initForm(): void {
    this.formGroup = new FormGroup({
      totalNumberOfFatalities: new FormControl('', Validators.required),
      numberOfPerpetratorFatalities: new FormControl('', Validators.required),
      totalNumberOfInjured: new FormControl('', Validators.required),
      numberOfPerpetratorInjured: new FormControl('', Validators.required),
      valueOfPropertyDamage: new FormControl('', Validators.required),
    });
  }

  validate(control: AbstractControl): ValidationErrors {
    console.log('validate2');
    return this.formGroup.valid
      ? null
      : {
          invalidForm: {
            valid: false,
            message: 'Form fields are invalid.',
          },
        };
  }
}
