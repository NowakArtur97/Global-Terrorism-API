import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { Store } from '@ngrx/store';
import CityDTO from 'src/app/city/models/city.dto';
import CountryDTO from 'src/app/country/models/country.dto';
import ProvinceDTO from 'src/app/province/models/province.dto';
import AppStoreState from 'src/app/store/app.store.state';
import TargetDTO from 'src/app/target/models/target.dto';
import VictimDTO from 'src/app/victim/models/victim.dto';

import EventDTO from '../models/event.dto';
import * as EventActions from '../store/event.actions';

// import * as EventActions from '../store/event.actions'
@Component({
  selector: 'app-event-form-wrapper',
  templateUrl: './event-form-wrapper.component.html',
  styleUrls: ['./event-form-wrapper.component.css'],
})
export class EventFormWrapperComponent implements OnInit {
  eventForm: FormGroup;
  isLoading = false;

  constructor(private store: Store<AppStoreState>) {}

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
    const event = this.getEventFromForm();

    console.log(event);
    this.store.dispatch(EventActions.addEvent({ event }));
  }
}
