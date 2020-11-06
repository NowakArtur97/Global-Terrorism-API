import { Component, forwardRef } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, NG_VALIDATORS, NG_VALUE_ACCESSOR, Validators } from '@angular/forms';
import { AbstractFormComponent } from 'src/app/common/components/abstract-form.component';
import CommonValidators from 'src/app/common/validators/common.validator';
import { selectEventToUpdate } from 'src/app/event/store/event.reducer';

import Event from '../../event/models//event.model';

@Component({
  selector: 'app-city-form',
  templateUrl: './city-form.component.html',
  styleUrls: ['./city-form.component.css'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => CityFormComponent),
      multi: true,
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => CityFormComponent),
      multi: true,
    },
  ],
})
export class CityFormComponent extends AbstractFormComponent {
  initForm(): void {
    let name = '';
    let latitude = 0;
    let longitude = 0;

    this.updateSubscription$ = this.store
      .select(selectEventToUpdate)
      .subscribe((event: Event) => {
        if (event?.city) {
          const { city } = event;
          name = city.name;
          latitude = city.latitude;
          longitude = city.longitude;
        }
      });

    this.formGroup = new FormGroup({
      name: new FormControl(name, [CommonValidators.notBlank]),
      latitude: new FormControl(latitude, [
        Validators.min(-90),
        Validators.max(90),
        CommonValidators.notBlank,
      ]),
      longitude: new FormControl(longitude, [
        Validators.min(-180),
        Validators.max(180),
        CommonValidators.notBlank,
      ]),
    });
  }

  get name(): AbstractControl {
    return this.formGroup.get('name');
  }

  get latitude(): AbstractControl {
    return this.formGroup.get('latitude');
  }

  get longitude(): AbstractControl {
    return this.formGroup.get('longitude');
  }
}
