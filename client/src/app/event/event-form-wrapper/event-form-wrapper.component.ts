import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { Store } from '@ngrx/store';
import { Subscription } from 'rxjs/internal/Subscription';
import CityDTO from 'src/app/city/models/city.dto';
import CountryDTO from 'src/app/country/models/country.dto';
import ProvinceDTO from 'src/app/province/models/province.dto';
import AppStoreState from 'src/app/store/app.state';
import TargetDTO from 'src/app/target/models/target.dto';
import VictimDTO from 'src/app/victim/models/victim.dto';

import EventDTO from '../models/event.dto';
import Event from '../models/event.model';
import * as EventActions from '../store/event.actions';
import { selectEventToUpdate } from '../store/event.reducer';
import EventMapper from '../utils/event.mapper';

@Component({
  selector: 'app-event-form-wrapper',
  templateUrl: './event-form-wrapper.component.html',
  styleUrls: ['./event-form-wrapper.component.css'],
})
export class EventFormWrapperComponent implements OnInit {
  private updateSubscription$ = new Subscription();
  private eventToUpdate: Event;
  title: string;
  eventForm: FormGroup;
  isUpdating = false;
  isLoading = false;

  constructor(private store: Store<AppStoreState>) {}

  ngOnInit(): void {
    this.initForm();
    this.updateSubscription$.add(
      this.store.select(selectEventToUpdate).subscribe((eventToUpdate) => {
        this.isUpdating = !!eventToUpdate;
        this.title = this.isUpdating ? 'Update' : 'Add';
        this.eventForm.updateValueAndValidity();
        this.eventToUpdate = eventToUpdate;
        this.initForm();
      })
    );
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
    if (this.eventToUpdate) {
      const event = this.eventToUpdate;
      const {
        summary,
        motive,
        date,
        isPartOfMultipleIncidents,
        isSuccessful,
        isSuicidal,
        target,
        city,
        victim,
      } = event;
      const { name, latitude, longitude } = city;
      const {
        totalNumberOfFatalities,
        numberOfPerpetratorFatalities,
        totalNumberOfInjured,
        numberOfPerpetratorInjured,
        valueOfPropertyDamage,
      } = victim;
      this.eventForm.get('event').setValue({
        summary,
        motive,
        date,
        isPartOfMultipleIncidents,
        isSuccessful,
        isSuicidal,
      });
      this.eventForm.get('target').setValue({ target: target.target });
      this.eventForm.get('city').setValue({
        name,
        latitude,
        longitude,
      });
      this.eventForm.get('victim').setValue({
        totalNumberOfFatalities,
        numberOfPerpetratorFatalities,
        totalNumberOfInjured,
        numberOfPerpetratorInjured,
        valueOfPropertyDamage,
      });
      this.eventForm.get('province').setValue({ name: city.province.name });
      this.eventForm
        .get('country')
        .setValue({ name: city.province.country.name });
    } else {
      this.eventForm = new FormGroup({
        event: new FormControl(''),
        target: new FormControl(''),
        city: new FormControl(''),
        victim: new FormControl(''),
        province: new FormControl(''),
        country: new FormControl(''),
      });
    }
  }

  onSubmitForm(): void {
    console.log(this.eventForm.value);
    const eventDTO = this.getEventFromForm();
    console.log(eventDTO);
    if (this.isUpdating) {
      this.store.dispatch(
        EventActions.updateEvent({
          eventToUpdate: {
            id: eventDTO.id,
            changes: EventMapper.mapToModel(eventDTO),
          },
        })
      );
      this.store.dispatch(
        EventActions.updateEventFinish({
          eventToUpdate: eventDTO,
        })
      );
    } else {
      this.store.dispatch(EventActions.addEventStart({ event: eventDTO }));
    }
  }
}
