import { Component, forwardRef } from '@angular/core';
import { FormControl, FormGroup, NG_VALIDATORS, NG_VALUE_ACCESSOR, Validators } from '@angular/forms';
import { AbstractForm } from 'src/app/common/components/abstract-form.component';

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
export class EventFormComponent extends AbstractForm {
  initForm(): void {
    this.formGroup = new FormGroup({
      summary: new FormControl('', Validators.required),
      motive: new FormControl('', Validators.required),
      date: new FormControl('', Validators.required),
      isPartOfMultipleIncidents: new FormControl('false', Validators.required),
      isSuccessful: new FormControl('false', Validators.required),
      isSuicidal: new FormControl('false', Validators.required),
    });
  }
}
