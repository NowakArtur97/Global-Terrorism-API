import { FormControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

export default class CommonValidators {
  static notBlank(formControl: FormControl): ValidationErrors {
    if (formControl.value?.trim().length > 0) {
      return null;
    }
    return { notBlank: true };
  }

  static withoutSpaces(formControl: FormControl): ValidationErrors {
    if (formControl.value?.includes(' ')) {
      return { withoutSpaces: true };
    }
    return null;
  }

  static notThreeRepetitiveCharacters(
    formControl: FormControl
  ): ValidationErrors {
    const characters = formControl.value.split('');
    const maxRepetitiveCharacters = 2;
    let hasRepetetiveCharacters = false;
    for (let i = 0; i < characters.length - maxRepetitiveCharacters; i++) {
      if (
        characters[i] === characters[i + 1] &&
        characters[i] === characters[i + 2]
      ) {
        hasRepetetiveCharacters = true;
        break;
      }
    }
    if (hasRepetetiveCharacters) {
      return { repetitiveCharacters: true };
    }
    return null;
  }

  static notMatch(
    controlName: string,
    matchingControlName: string
  ): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors => {
      const control = formGroup.controls[controlName];
      const matchingControl = formGroup.controls[matchingControlName];
      if (control.value !== matchingControl.value) {
        control.setErrors({
          ...control.errors,
          notMatch: true,
        });
        matchingControl.setErrors({
          ...matchingControl.errors,
          notMatch: true,
        });
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
