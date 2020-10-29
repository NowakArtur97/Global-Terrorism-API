import { AbstractControl, ControlValueAccessor, FormGroup, ValidationErrors, Validator } from '@angular/forms';

export abstract class AbstractForm implements ControlValueAccessor, Validator {
  formGroup: FormGroup;

  abstract initForm(): void;

  writeValue(val: any): void {
    if (val) {
      this.formGroup.setValue(val, { emitEvent: false });
      console.log('writeValue2');
    }
  }

  registerOnChange(fn: any): void {
    this.formGroup.valueChanges.subscribe(fn);
    console.log('registerOnChange2');
  }

  registerOnTouched(fn: any): void {
    console.log('registerOnTouched2');
  }

  setDisabledState?(isDisabled: boolean): void {
    isDisabled ? this.formGroup.disable() : this.formGroup.enable();
    console.log('setDisabledState2');
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

  registerOnValidatorChange?(fn: () => void): void {
    console.log('registerOnValidatorChange2');
  }
}
