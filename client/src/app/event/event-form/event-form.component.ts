import { Component, forwardRef } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, NG_VALIDATORS, NG_VALUE_ACCESSOR } from '@angular/forms';
import { AbstractFormComponent } from 'src/app/common/components/abstract-form.component';
import CommonValidators from 'src/app/common/validators/common.validator';

import Event from '../models/event.model';
import { selectEventToUpdate } from '../store/event.reducer';

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
    let summary = '';
    let motive = '';
    let date = new Date();
    let isPartOfMultipleIncidents = 'false';
    let isSuccessful = 'false';
    let isSuicidal = 'false';

    this.updateSubscription$.add(
      this.store.select(selectEventToUpdate).subscribe((event: Event) => {
        if (event) {
          summary = event.summary;
          motive = event.motive;
          date = event.date;
          isPartOfMultipleIncidents = event.isPartOfMultipleIncidents + '';
          isSuccessful = event.isSuccessful + '';
          isSuicidal = event.isSuicidal + '';
        }
      })
    );

    this.formGroup = new FormGroup({
      summary: new FormControl(summary, [CommonValidators.notBlank]),
      motive: new FormControl(motive, [CommonValidators.notBlank]),
      date: new FormControl('', [
        CommonValidators.notBlank,
        CommonValidators.dateInPast,
      ]),
      isPartOfMultipleIncidents: new FormControl(isPartOfMultipleIncidents, [
        CommonValidators.notBlank,
      ]),
      isSuccessful: new FormControl(isSuccessful, [CommonValidators.notBlank]),
      isSuicidal: new FormControl(isSuicidal, [CommonValidators.notBlank]),
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
