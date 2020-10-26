import { Component, OnInit } from '@angular/core';
import { ControlValueAccessor, FormControl, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-victim-form',
  templateUrl: './victim-form.component.html',
  styleUrls: ['./victim-form.component.css'],
})
export class VictimFormComponent implements OnInit, ControlValueAccessor {
  victimForm: FormGroup;

  constructor() {}

  ngOnInit(): void {
    this.initForm();
  }

  initForm(): void {
    this.victimForm = new FormGroup({
      totalNumberOfFatalities: new FormControl('', Validators.required),
      numberOfPerpetratorFatalities: new FormControl('', Validators.required),
      totalNumberOfInjured: new FormControl('', Validators.required),
      numberOfPerpetratorInjured: new FormControl('', Validators.required),
      valueOfPropertyDamage: new FormControl('', Validators.required),
    });
  }

  writeValue(val: any): void {
    val && this.victimForm.setValue(val, { emitEvent: false });
  }

  registerOnChange(fn: any): void {
    this.victimForm.valueChanges.subscribe(fn);
  }

  registerOnTouched(fn: any): void {}

  setDisabledState?(isDisabled: boolean): void {
    isDisabled ? this.victimForm.disable() : this.victimForm.enable();
  }
}
