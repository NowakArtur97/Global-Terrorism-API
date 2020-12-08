import { AfterViewChecked, ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
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

@Component({
  selector: 'app-event-form-wrapper',
  templateUrl: './event-form-wrapper.component.html',
  styleUrls: ['./event-form-wrapper.component.css'],
})
export class EventFormWrapperComponent
  implements OnInit, OnDestroy, AfterViewChecked {
  private updateSubscription$: Subscription;
  private eventToUpdate: Event;
  errorMessages: string[] = [];
  action: string;
  eventForm: FormGroup;
  isUpdating = false;
  isLoading = false;

  constructor(
    private store: Store<AppStoreState>,
    private changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.updateSubscription$ = this.store
      .select('event')
      .subscribe(({ eventToUpdate, isLoading, errorMessages }) => {
        this.isUpdating = !!eventToUpdate;
        this.action = this.isUpdating ? 'Update' : 'Add';
        this.eventToUpdate = eventToUpdate;
        this.isLoading = isLoading;
        this.errorMessages = errorMessages;
        this.initForm();
      });
  }

  ngOnDestroy(): void {
    this.updateSubscription$?.unsubscribe();
  }

  ngAfterViewChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  private initForm(): void {
    if (this.eventToUpdate) {
      this.initFormWithDataToUpdate();
    } else {
      this.initFormWithDefaultValue();
    }
  }

  onSubmitForm(): void {
    const eventDTO = this.getEventFromForm();
    if (this.isUpdating) {
      this.setEventIds(eventDTO);
      this.store.dispatch(
        EventActions.updateEvent({
          eventDTO,
        })
      );
    } else {
      this.store.dispatch(EventActions.addEventStart({ eventDTO }));
    }
  }

  private initFormWithDefaultValue(): void {
    this.eventForm = new FormGroup({
      event: new FormControl({
        summary: '',
        motive: '',
        date: new Date(),
        isPartOfMultipleIncidents: 'false',
        isSuccessful: 'false',
        isSuicidal: 'false',
      }),
      target: new FormControl({ target: '' }),
      city: new FormControl({
        name: '',
        latitude: 0,
        longitude: 0,
      }),
      victim: new FormControl({
        totalNumberOfFatalities: 0,
        numberOfPerpetratorsFatalities: 0,
        totalNumberOfInjured: 0,
        numberOfPerpetratorsInjured: 0,
        valueOfPropertyDamage: 0,
      }),
      province: new FormControl({ name: '' }),
      country: new FormControl({ name: '' }),
    });
  }

  private initFormWithDataToUpdate(): void {
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
      numberOfPerpetratorsFatalities,
      totalNumberOfInjured,
      numberOfPerpetratorsInjured,
      valueOfPropertyDamage,
    } = victim;

    this.eventForm = new FormGroup({
      event: new FormControl({
        summary,
        motive,
        date: new Date(date),
        isPartOfMultipleIncidents: isPartOfMultipleIncidents + '',
        isSuccessful: isSuccessful + '',
        isSuicidal: isSuicidal + '',
      }),
      target: new FormControl({ target: target.target }),
      city: new FormControl({
        name,
        latitude,
        longitude,
      }),
      victim: new FormControl({
        totalNumberOfFatalities,
        numberOfPerpetratorsFatalities,
        totalNumberOfInjured,
        numberOfPerpetratorsInjured,
        valueOfPropertyDamage,
      }),
      province: new FormControl({ name: city.province.name }),
      country: new FormControl({ name: city.province.country.name }),
    });
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
      numberOfPerpetratorsFatalities: victim.numberOfPerpetratorsFatalities,
      totalNumberOfInjured: victim.totalNumberOfInjured,
      numberOfPerpetratorsInjured: victim.numberOfPerpetratorsInjured,
      valueOfPropertyDamage: victim.valueOfPropertyDamage,
    };
    const targetDTO: TargetDTO = {
      target: target.target,
      countryOfOrigin: countryDTO,
    };
    const date =
      event.date.getFullYear() +
      '-' +
      (event.date.getMonth() + 1) +
      '-' +
      event.date.getDate();
    return {
      summary: event.summary,
      motive: event.motive,
      date,
      isPartOfMultipleIncidents:
        event.isPartOfMultipleIncidents + '' === 'true',
      isSuccessful: event.isSuccessful + '' === 'true',
      isSuicidal: event.isSuicidal + '' === 'true',
      target: targetDTO,
      city: cityDTO,
      victim: victimDTO,
    };
  }

  private setEventIds(eventDTO: EventDTO): void {
    const { id, target, city, victim } = this.eventToUpdate;
    eventDTO.id = id;
    eventDTO.target.id = target.id;
    eventDTO.city.id = city.id;
    eventDTO.victim.id = victim.id;
    eventDTO.city.province.id = city.province.id;
    eventDTO.city.province.country.id = city.province.country.id;
  }
}
