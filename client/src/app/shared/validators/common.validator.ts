import { FormControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

export default class CommonValidators {
  static notBlank(formControl: FormControl): ValidationErrors {
    if (formControl?.value?.trim().length > 0) {
      return null;
    }
    return { notBlank: true };
  }

  static mustMatch(
    controlName: string,
    matchingControlName: string
  ): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors => {
      const control = formGroup.controls[controlName];
      const matchingControl = formGroup.controls[matchingControlName];
      if (control.value !== matchingControl.value) {
        control.setErrors({ ...control.errors, mustMatch: true });
        matchingControl.setErrors({ ...control.errors, mustMatch: true });
      }
      return;
    };
  }

  static notInclude(
    controlNameToCheck: string,
    controlNameToNotBeIncluded: string
  ): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors => {
      const controlToCheck = formGroup.controls[controlNameToCheck];
      const controlToNotBeIncluded =
        formGroup.controls[controlNameToNotBeIncluded];

      if (controlToCheck.value.includes(controlToNotBeIncluded.value)) {
        controlToCheck.setErrors({
          ...controlToCheck.errors,
          notInclude: true,
        });
      }
      return;
    };
  }
}
