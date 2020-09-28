import { FormControl, ValidationErrors } from '@angular/forms';

export default class UserNameValidator {
  static notBlank = (formControl: FormControl): ValidationErrors =>
    formControl?.value?.trim().length > 0 ? null : { blank: true };
}
