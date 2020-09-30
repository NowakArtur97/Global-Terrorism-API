import { FormControl, ValidationErrors } from '@angular/forms';

export default class PasswordValidators {
  private static commonPasswords: string[] = [
    '123456',
    '123456789',
    'qwerty',
    'password',
    '1234567',
    '12345678',
    '12345',
    'iloveyou',
    '111111',
    '123123',
    'abc123',
    'qwerty123',
    '1q2w3e4r',
    'admin',
    'qwertyuiop',
    '654321',
    '555555',
    'lovely',
    '7777777',
    '888888',
    'princess',
    'dragon',
    'password1',
    '123qwe',
    '666666',
  ];

  static notPopular = (formControl: FormControl): ValidationErrors =>
    PasswordValidators.commonPasswords.includes(formControl.value)
      ? { notPopular: true }
      : null;

  static withoutUppercase = (formControl: FormControl): ValidationErrors =>
    /[A-Z]+/.test(formControl.value) ? null : { withoutUppercase: true };

  static withoutLowercase = (formControl: FormControl): ValidationErrors =>
    /[a-z]+/.test(formControl.value) ? null : { withoutLowercase: true };
}
