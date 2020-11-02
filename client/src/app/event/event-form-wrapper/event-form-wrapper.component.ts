import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import CountryDTO from 'src/app/country/models/country.dto';

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
    const {
      event,
      target,
      city,
      victim,
      province,
      country,
    } = this.eventForm.value;
    const countryDTO = new CountryDTO(country.name);
  }
}
