import { Component, forwardRef } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, NG_VALIDATORS, NG_VALUE_ACCESSOR } from '@angular/forms';
import { AbstractFormComponent } from 'src/app/common/components/abstract-form.component';
import CommonValidators from 'src/app/common/validators/common.validator';

@Component({
  selector: 'app-event-form',
  templateUrl: './event-form.component.html',
  styleUrls: ['./event-form.component.css'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => EventFormComponent),
      multi: true,
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => EventFormComponent),
      multi: true,
    },
  ],
})
export class EventFormComponent extends AbstractFormComponent {
  initForm(): void {
    this.formGroup = new FormGroup({
      summary: new FormControl('', [CommonValidators.notBlank]),
      motive: new FormControl('', [CommonValidators.notBlank]),
      date: new FormControl('', [CommonValidators.notBlank]),
      isPartOfMultipleIncidents: new FormControl('false', [
        CommonValidators.notBlank,
      ]),
      isSuccessful: new FormControl('false', [CommonValidators.notBlank]),
      isSuicidal: new FormControl('false', [CommonValidators.notBlank]),
    });
  }

  get summary(): AbstractControl {
    return this.formGroup.get('summary');
  }

  get motive(): AbstractControl {
    return this.formGroup.get('motive');
  }

  get date(): AbstractControl {
    return this.formGroup.get('date');
  }

  get isPartOfMultipleIncidents(): AbstractControl {
    return this.formGroup.get('isPartOfMultipleIncidents');
  }

  get isSuccessful(): AbstractControl {
    return this.formGroup.get('isSuccessful');
  }

  get isSuicidal(): AbstractControl {
    return this.formGroup.get('isSuicidal');
  }
}
