import { Component, forwardRef, OnDestroy } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, NG_VALIDATORS, NG_VALUE_ACCESSOR, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { AbstractFormComponent } from 'src/app/common/components/abstract-form.component';
import CommonValidators from 'src/app/common/validators/common.validator';

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
    this.formGroup = new FormGroup(
      {
        totalNumberOfFatalities: new FormControl('', [
          Validators.min(0),
          CommonValidators.notBlank,
        ]),
        numberOfPerpetratorFatalities: new FormControl('', [
          Validators.min(0),
          CommonValidators.notBlank,
        ]),
        totalNumberOfInjured: new FormControl('', [
          Validators.min(0),
          CommonValidators.notBlank,
        ]),
        numberOfPerpetratorInjured: new FormControl('', [
          Validators.min(0),
          CommonValidators.notBlank,
        ]),
        valueOfPropertyDamage: new FormControl('', [
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
