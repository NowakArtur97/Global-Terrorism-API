import { Component, forwardRef } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, NG_VALIDATORS, NG_VALUE_ACCESSOR } from '@angular/forms';
import { AbstractFormComponent } from 'src/app/common/components/abstract-form.component';
import CommonValidators from 'src/app/common/validators/common.validator';
import { selectEventToUpdate } from 'src/app/event/store/event.reducer';

import Event from '../../event/models//event.model';

@Component({
  selector: 'app-target-form',
  templateUrl: './target-form.component.html',
  styleUrls: ['./target-form.component.css'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => TargetFormComponent),
      multi: true,
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => TargetFormComponent),
      multi: true,
    },
  ],
})
export class TargetFormComponent extends AbstractFormComponent {
  initForm(): void {
    let targetName = '';

    this.store.select(selectEventToUpdate).subscribe((event: Event) => {
      if (event?.target) {
        const target = event.target;
        targetName = target.target;
      }
    });

    this.formGroup = new FormGroup({
      target: new FormControl(targetName, [CommonValidators.notBlank]),
    });
  }

  get target(): AbstractControl {
    return this.formGroup.get('target');
  }
}
