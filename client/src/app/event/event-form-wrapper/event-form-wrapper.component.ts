import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormControl, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-event-form-wrapper',
  templateUrl: './event-form-wrapper.component.html',
  styleUrls: ['./event-form-wrapper.component.css'],
})
export class EventFormWrapperComponent implements OnInit {
  eventForm: FormGroup;
  isLoading = false;

  constructor() {}

  ngOnInit(): void {
    this.initForm();
  }

  initForm(): void {
    this.eventForm = new FormGroup({
      event: new FormControl(''),
      target: new FormControl(''),
      city: new FormControl(''),
      victim: new FormControl(''),
      province: new FormControl(''),
      country: new FormControl(''),
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

  get countryName(): AbstractControl {
    return this.eventForm.get('country.name');
  }
}
