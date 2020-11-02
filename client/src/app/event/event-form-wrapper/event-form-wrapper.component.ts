import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import CityDTO from 'src/app/city/models/city.dto';
import CountryDTO from 'src/app/country/models/country.dto';
import ProvinceDTO from 'src/app/province/models/province.dto';
import TargetDTO from 'src/app/target/models/target.dto';
import VictimDTO from 'src/app/victim/models/victim.dto';

import EventDTO from '../models/event.dto';

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

  private getEventFromForm(): EventDTO {
    const {
      event,
      target,
      city,
      victim,
      province,
      country,
    } = this.eventForm.value;
    const countryDTO = new CountryDTO(country.name);
    const provinceDTO = new ProvinceDTO(province.name, countryDTO);
    const cityDTO = new CityDTO(
      city.name,
      city.latitude,
      city.longitude,
      provinceDTO
    );
    const victimDTO = new VictimDTO(
      victim.totalNumberOfFatalities,
      victim.numberOfPerpetratorFatalities,
      victim.totalNumberOfInjured,
      victim.numberOfPerpetratorInjured,
      victim.valueOfPropertyDamage
    );
    const targetDTO = new TargetDTO(target.target, countryDTO);
    return new EventDTO(
      event.summary,
      event.motive,
      event.date,
      event.isPartOfMultipleIncidents,
      event.isSuccessful,
      event.isSuicidal,
      targetDTO,
      cityDTO,
      victimDTO
    );
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
    const eventDTO = this.getEventFromForm();

    console.log(eventDTO);
  }
}
