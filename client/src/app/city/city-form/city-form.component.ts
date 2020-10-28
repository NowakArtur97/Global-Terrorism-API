import { Component, OnInit } from '@angular/core';
import {
  AbstractControl,
  ControlValueAccessor,
  FormControl,
  FormGroup,
  ValidationErrors,
  Validator,
  Validators,
} from '@angular/forms';

@Component({
  selector: 'app-city-form',
  templateUrl: './city-form.component.html',
  styleUrls: ['./city-form.component.css'],
})
export class CityFormComponent
  implements OnInit, ControlValueAccessor, Validator {
  cityForm: FormGroup;

  constructor() {}

  ngOnInit(): void {
    this.initForm();
  }

  initForm(): void {
    this.cityForm = new FormGroup({
      name: new FormControl('', Validators.required),
      latitude: new FormControl('', Validators.required),
      longitude: new FormControl('', Validators.required),
    });
  }

  writeValue(val: any): void {
    if (val) {
      this.cityForm.setValue(val, { emitEvent: false });
    }
  }

  registerOnChange(fn: any): void {
    this.cityForm.valueChanges.subscribe(fn);
  }

  registerOnTouched(fn: any): void {}

  setDisabledState?(isDisabled: boolean): void {
    isDisabled ? this.cityForm.disable() : this.cityForm.enable();
  }

  validate(control: AbstractControl): ValidationErrors {
    return this.cityForm.valid
      ? null
      : {
          invalidForm: {
            valid: false,
            message: 'City fields are invalid.',
          },
        };
  }

  registerOnValidatorChange?(fn: () => void): void {}
}
