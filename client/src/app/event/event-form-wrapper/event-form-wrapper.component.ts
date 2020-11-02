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
    const countryDTO: CountryDTO = { name: country.name };
    const provinceDTO: ProvinceDTO = {
      name: province.name,
      country: countryDTO,
    };
    const cityDTO: CityDTO = {
      name: city.name,
      latitude: city.latitude,
      longitude: city.longitude,
      province: provinceDTO,
    };
    const victimDTO: VictimDTO = {
      totalNumberOfFatalities: victim.totalNumberOfFatalities,
      numberOfPerpetratorFatalities: victim.numberOfPerpetratorFatalities,
      totalNumberOfInjured: victim.totalNumberOfInjured,
      numberOfPerpetratorInjured: victim.numberOfPerpetratorInjured,
      valueOfPropertyDamage: victim.valueOfPropertyDamage,
    };
    const targetDTO: TargetDTO = {
      target: target.target,
      countryOfOrigin: countryDTO,
    };
    return {
      summary: event.summary,
      motive: event.motive,
      date: event.date,
      isPartOfMultipleIncidents: event.isPartOfMultipleIncidents,
      isSuccessful: event.isSuccessful,
      isSuicidal: event.isSuicidal,
      target: targetDTO,
      city: cityDTO,
      victim: victimDTO,
    };
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

  onAddEvent(): void {
    const event = this.getEventFromForm();
    this.store.dispatch(EventActions.addEvent({ event }));
  }
}
