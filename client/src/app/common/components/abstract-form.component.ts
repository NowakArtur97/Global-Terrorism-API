import { Component, OnInit } from '@angular/core';
import { AbstractControl, ControlValueAccessor, FormGroup, ValidationErrors, Validator } from '@angular/forms';

@Component({ template: '' })
export abstract class AbstractForm
  implements OnInit, ControlValueAccessor, Validator {
  formGroup: FormGroup;

  ngOnInit(): void {
    this.initForm();
  }

  abstract initForm(): void;

  writeValue(val: any): void {
    if (val) {
      this.formGroup.setValue(val, { emitEvent: false });
    }
  }

  registerOnChange(fn: any): void {
    this.formGroup.valueChanges.subscribe(fn);
  }

  registerOnTouched(fn: any): void {}

  setDisabledState?(isDisabled: boolean): void {
    isDisabled ? this.formGroup.disable() : this.formGroup.enable();
  }

  validate(control: AbstractControl): ValidationErrors {
    return this.formGroup.valid
      ? null
      : {
          invalidForm: {
            valid: false,
            message: 'Form fields are invalid.',
          },
        };
  }

  registerOnValidatorChange?(fn: () => void): void {}
}
