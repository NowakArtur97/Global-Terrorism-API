import { Component, forwardRef, OnDestroy } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, NG_VALIDATORS, NG_VALUE_ACCESSOR, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { AbstractFormComponent } from 'src/app/common/components/abstract-form.component';
import CommonValidators from 'src/app/common/validators/common.validator';
import { selectEventToUpdate } from 'src/app/event/store/event.reducer';

import Event from '../../event/models//event.model';

@Component({
  selector: 'app-victim-form',
  templateUrl: './victim-form.component.html',
  styleUrls: ['./victim-form.component.css'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => VictimFormComponent),
      multi: true,
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => VictimFormComponent),
      multi: true,
    },
  ],
})
export class VictimFormComponent
  extends AbstractFormComponent
  implements OnDestroy {
  private victimFormSubscriptions$ = new Subscription();

  ngOnDestroy(): void {
    super.ngOnDestroy();
    this.victimFormSubscriptions$.unsubscribe();
  }

  initForm(): void {
    let totalNumberOfFatalities = 0;
    let numberOfPerpetratorsFatalities = 0;
    let totalNumberOfInjured = 0;
    let numberOfPerpetratorsInjured = 0;
    let valueOfPropertyDamage = 0;

    this.updateSubscription$.add(
      this.store.select(selectEventToUpdate).subscribe((event: Event) => {
        if (event?.victim) {
          const { victim } = event;
          totalNumberOfFatalities = victim.totalNumberOfFatalities;
          numberOfPerpetratorsFatalities =
            victim.numberOfPerpetratorsFatalities;
          totalNumberOfInjured = victim.totalNumberOfInjured;
          numberOfPerpetratorsInjured = victim.numberOfPerpetratorsInjured;
          valueOfPropertyDamage = victim.valueOfPropertyDamage;
        }
      })
    );

    this.formGroup = new FormGroup(
      {
        totalNumberOfFatalities: new FormControl(totalNumberOfFatalities, [
          Validators.min(0),
          CommonValidators.notBlank,
        ]),
        numberOfPerpetratorsFatalities: new FormControl(
          numberOfPerpetratorsFatalities,
          [Validators.min(0), CommonValidators.notBlank]
        ),
        totalNumberOfInjured: new FormControl(totalNumberOfInjured, [
          Validators.min(0),
          CommonValidators.notBlank,
        ]),
        numberOfPerpetratorsInjured: new FormControl(
          numberOfPerpetratorsInjured,
          [Validators.min(0), CommonValidators.notBlank]
        ),
        valueOfPropertyDamage: new FormControl(valueOfPropertyDamage, [
          Validators.min(0),
          CommonValidators.notBlank,
        ]),
      },
      {
        validators: [
          CommonValidators.lowerOrEqual(
            'totalNumberOfFatalities',
            'numberOfPerpetratorsFatalities'
          ),
          CommonValidators.lowerOrEqual(
            'totalNumberOfInjured',
            'numberOfPerpetratorsInjured'
          ),
          ,
        ],
      }
    );
    this.setupFormSubscriptions();
  }

  private setupFormSubscriptions(): void {
    this.victimFormSubscriptions$.add(
      this.totalNumberOfFatalities.valueChanges.subscribe(() =>
        this.numberOfPerpetratorsFatalities.updateValueAndValidity()
      )
    );

    this.victimFormSubscriptions$.add(
      this.totalNumberOfInjured.valueChanges.subscribe(() =>
        this.numberOfPerpetratorsInjured.updateValueAndValidity()
      )
    );
  }

  get totalNumberOfFatalities(): AbstractControl {
    return this.formGroup.get('totalNumberOfFatalities');
  }

  get numberOfPerpetratorsFatalities(): AbstractControl {
    return this.formGroup.get('numberOfPerpetratorsFatalities');
  }

  get totalNumberOfInjured(): AbstractControl {
    return this.formGroup.get('totalNumberOfInjured');
  }

  get numberOfPerpetratorsInjured(): AbstractControl {
    return this.formGroup.get('numberOfPerpetratorsInjured');
  }

  get valueOfPropertyDamage(): AbstractControl {
    return this.formGroup.get('valueOfPropertyDamage');
  }
}
