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
    this.victimFormSubscriptions$.unsubscribe();
  }

  initForm(): void {
    let totalNumberOfFatalities = 0;
    let numberOfPerpetratorFatalities = 0;
    let totalNumberOfInjured = 0;
    let numberOfPerpetratorInjured = 0;
    let valueOfPropertyDamage = 0;

    this.updateSubscription$.add(
      this.store.select(selectEventToUpdate).subscribe((event: Event) => {
        if (event?.victim) {
          const { victim } = event;
          totalNumberOfFatalities = victim.totalNumberOfFatalities;
          numberOfPerpetratorFatalities = victim.numberOfPerpetratorFatalities;
          totalNumberOfInjured = victim.totalNumberOfInjured;
          valueOfPropertyDamage = victim.numberOfPerpetratorInjured;
          totalNumberOfFatalities = victim.valueOfPropertyDamage;
        }
      })
    );

    this.formGroup = new FormGroup(
      {
        totalNumberOfFatalities: new FormControl(totalNumberOfFatalities, [
          Validators.min(0),
          CommonValidators.notBlank,
        ]),
        numberOfPerpetratorFatalities: new FormControl(
          numberOfPerpetratorFatalities,
          [Validators.min(0), CommonValidators.notBlank]
        ),
        totalNumberOfInjured: new FormControl(totalNumberOfInjured, [
          Validators.min(0),
          CommonValidators.notBlank,
        ]),
        numberOfPerpetratorInjured: new FormControl(
          numberOfPerpetratorInjured,
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
            'numberOfPerpetratorFatalities'
          ),
          CommonValidators.lowerOrEqual(
            'totalNumberOfInjured',
            'numberOfPerpetratorInjured'
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
        this.numberOfPerpetratorFatalities.updateValueAndValidity()
      )
    );

    this.victimFormSubscriptions$.add(
      this.totalNumberOfInjured.valueChanges.subscribe(() =>
        this.numberOfPerpetratorInjured.updateValueAndValidity()
      )
    );
  }

  get totalNumberOfFatalities(): AbstractControl {
    return this.formGroup.get('totalNumberOfFatalities');
  }

  get numberOfPerpetratorFatalities(): AbstractControl {
    return this.formGroup.get('numberOfPerpetratorFatalities');
  }

  get totalNumberOfInjured(): AbstractControl {
    return this.formGroup.get('totalNumberOfInjured');
  }

  get numberOfPerpetratorInjured(): AbstractControl {
    return this.formGroup.get('numberOfPerpetratorInjured');
  }

  get valueOfPropertyDamage(): AbstractControl {
    return this.formGroup.get('valueOfPropertyDamage');
  }
}
