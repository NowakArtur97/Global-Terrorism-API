import { FormControl, ValidationErrors } from '@angular/forms';

export default class CommonValidators {
  static notBlank(formControl: FormControl): ValidationErrors {
    if (formControl?.value?.trim().length > 0) {
      return null;
    }
    return { notBlank: true };
  }
}
