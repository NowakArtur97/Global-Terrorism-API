import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-event-form',
  templateUrl: './event-form.component.html',
  styleUrls: ['./event-form.component.css'],
})
export class EventFormComponent implements OnInit {
  eventForm: FormGroup;
  isLoading = false;

  constructor() {}

  ngOnInit(): void {
    this.initForm();
  }

  initForm(): void {
    this.eventForm = new FormGroup({
      event: new FormGroup({
        summary: new FormControl('', Validators.required),
        motive: new FormControl('', Validators.required),
        date: new FormControl('', Validators.required),
        isPartOfMultipleIncidents: new FormControl(
          'false',
          Validators.required
        ),
        isSuccessful: new FormControl('false', Validators.required),
        isSuicidal: new FormControl('false', Validators.required),
      }),
      target: new FormGroup({
        target: new FormControl('', Validators.required),
      }),
      victim: new FormGroup({
        totalNumberOfFatalities: new FormControl('', Validators.required),
        numberOfPerpetratorFatalities: new FormControl('', Validators.required),
        totalNumberOfInjured: new FormControl('', Validators.required),
        numberOfPerpetratorInjured: new FormControl('', Validators.required),
        valueOfPropertyDamage: new FormControl('', Validators.required),
      }),
    });
  }

  onAddForm(): void {
    console.log(this.eventForm);
    console.log(this.eventForm.value);
  }

  get summary(): AbstractControl {
    return this.eventForm.get('event.summary');
  }

  get motive(): AbstractControl {
    return this.eventForm.get('event.motive');
  }

  get date(): AbstractControl {
    return this.eventForm.get('event.date');
  }

  get isPartOfMultipleIncidents(): AbstractControl {
    return this.eventForm.get('event.isPartOfMultipleIncidents');
  }

  get isSuccessful(): AbstractControl {
    return this.eventForm.get('event.isSuccessful');
  }

  get isSuicidal(): AbstractControl {
    return this.eventForm.get('event.isSuicidal');
  }

  get target(): AbstractControl {
    return this.eventForm.get('target.target');
  }

  get totalNumberOfFatalities(): AbstractControl {
    return this.eventForm.get('victim.totalNumberOfFatalities');
  }

  get numberOfPerpetratorFatalities(): AbstractControl {
    return this.eventForm.get('victim.numberOfPerpetratorFatalities');
  }

  get totalNumberOfInjured(): AbstractControl {
    return this.eventForm.get('victim.totalNumberOfInjured');
  }

  get numberOfPerpetratorInjured(): AbstractControl {
    return this.eventForm.get('victim.numberOfPerpetratorInjured');
  }

  get valueOfPropertyDamage(): AbstractControl {
    return this.eventForm.get('victim.valueOfPropertyDamage');
  }
}
