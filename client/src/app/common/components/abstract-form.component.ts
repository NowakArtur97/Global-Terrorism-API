import { Component, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, ControlValueAccessor, FormGroup, ValidationErrors, Validator } from '@angular/forms';
import { Store } from '@ngrx/store';
import { Subscription } from 'rxjs';
import AppStoreState from 'src/app/store/app.state';

@Component({ template: '' })
export abstract class AbstractFormComponent
  implements OnInit, OnDestroy, ControlValueAccessor, Validator {
  protected updateSubscription$ = new Subscription();
  formGroup: FormGroup;

  constructor(protected store: Store<AppStoreState>) {}

  ngOnInit(): void {
    this.initForm();
  }

  ngOnDestroy(): void {
    this.updateSubscription$?.unsubscribe();
    console.log('destroy');
  }

  abstract initForm(): void;

  writeValue(val: any): void {
    if (val) {
      this.formGroup.setValue(val, { emitEvent: false });
    }
  }

  registerOnChange(fn: any): void {
    this.formGroup.valueChanges.subscribe(fn);
  }

  registerOnTouched(fn: any): void {}

  setDisabledState?(isDisabled: boolean): void {
    isDisabled ? this.formGroup.disable() : this.formGroup.enable();
  }

  validate(control: AbstractControl): ValidationErrors {
    return this.formGroup.valid
      ? null
      : {
          invalidForm: {
            valid: false,
            message: 'Form fields are invalid.',
          },
        };
  }

  registerOnValidatorChange?(fn: () => void): void {}
}
