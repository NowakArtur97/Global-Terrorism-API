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

  private static withoutUppercase = (
    formControl: FormControl
  ): ValidationErrors =>
    /[A-Z]+/.test(formControl.value) ? null : { withoutUppercase: true };

  private static withoutLowercase = (
    formControl: FormControl
  ): ValidationErrors =>
    /[a-z]+/.test(formControl.value) ? null : { withoutLowercase: true };

  private static withoutDigit = (formControl: FormControl): ValidationErrors =>
    /[0-9]+/.test(formControl.value) ? null : { withoutDigits: true };

  private static withoutSpecial = (
    formControl: FormControl
  ): ValidationErrors =>
    /[\\!"#\$%&'()*\+,-.\/:;<=>?@\[\]^_`{|}~]/.test(formControl.value)
      ? null
      : { withoutSpecial: true };

  static notPopular = (formControl: FormControl): ValidationErrors =>
    PasswordValidators.commonPasswords.includes(formControl.value)
      ? { notPopular: true }
      : null;

  static characteristicRule(formControl: FormControl): ValidationErrors {
    const characteristicRules = [
      PasswordValidators.withoutUppercase,
      PasswordValidators.withoutLowercase,
      PasswordValidators.withoutDigit,
      PasswordValidators.withoutSpecial,
    ];
    const numberOfRequirementsToMeet = 2;

    if (formControl.errors) {
      const characteristicRulesErrors: ValidationErrors[] = [];
      characteristicRules.forEach((test) => {
        const result = test(formControl);
        if (result) {
          characteristicRulesErrors.push(result);
        }
      });

      const numberOfFulfilledRequirements =
        characteristicRules.length -
        characteristicRulesErrors.filter((value) => value).length;

      if (numberOfRequirementsToMeet > numberOfFulfilledRequirements) {
        return Object.assign({}, ...characteristicRulesErrors);
      }
    }
    return null;
  }
}
