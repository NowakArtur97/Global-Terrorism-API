import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { EffectsModule } from '@ngrx/effects';
import { Store, StoreModule } from '@ngrx/store';
import { of } from 'rxjs';
import { CityModule } from 'src/app/city/city.module';
import CityDTO from 'src/app/city/models/city.dto';
import City from 'src/app/city/models/city.model';
import { MaterialModule } from 'src/app/common/material.module';
import { CountryModule } from 'src/app/country/country.module';
import CountryDTO from 'src/app/country/models/country.dto';
import Country from 'src/app/country/models/country.model';
import ProvinceDTO from 'src/app/province/models/province.dto';
import Province from 'src/app/province/models/province.model';
import { ProvinceModule } from 'src/app/province/province.module';
import AppStoreState from 'src/app/store/app.state';
import TargetDTO from 'src/app/target/models/target.dto';
import Target from 'src/app/target/models/target.model';
import { TargetModule } from 'src/app/target/target.module';
import VictimDTO from 'src/app/victim/models/victim.dto';
import Victim from 'src/app/victim/models/victim.model';
import { VictimModule } from 'src/app/victim/victim.module';

import { EventFormComponent } from '../event-form/event-form.component';
import EventDTO from '../models/event.dto';
import Event from '../models/event.model';
import * as EventActions from '../store/event.actions';
import { EventStoreState, selectEventToUpdate } from '../store/event.reducer';
import { EventFormWrapperComponent } from './event-form-wrapper.component';

describe('EventFormWrapperComponent', () => {
  let component: EventFormWrapperComponent;
  let fixture: ComponentFixture<EventFormWrapperComponent>;
  let store: Store<AppStoreState>;
  const eventToUpdate = new Event(
    6,
    'summary',
    'motive',
    new Date(),
    false,
    false,
    false,
    new Target(3, 'target', new Country(1, 'country')),
    new City(
      4,
      'city',
      20,
      10,
      new Province(2, 'province', new Country(1, 'country'))
    ),
    new Victim(5, 11, 3, 14, 4, 2000)
  );

  const initialState: EventStoreState = {
    ids: [],
    entities: {},
    eventToUpdate: null,
  };

  const initialStateWithEventToUpdate: EventStoreState = {
    ids: [],
    entities: {},
    eventToUpdate,
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EventFormWrapperComponent, EventFormComponent],
      imports: [
        StoreModule.forRoot({}),
        ReactiveFormsModule,
        MaterialModule,
        BrowserAnimationsModule,
        EffectsModule.forRoot(),
        HttpClientTestingModule,

        VictimModule,
        TargetModule,
        CityModule,
        ProvinceModule,
        CountryModule,
      ],
      providers: [Store],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EventFormWrapperComponent);
    component = fixture.componentInstance;

    store = TestBed.inject(Store);

    spyOn(store, 'select').and.callFake((selector) => {
      if (selector === selectEventToUpdate) {
        return of(initialState.eventToUpdate);
      }
    });
    spyOn(store, 'dispatch');

    fixture.detectChanges();
    component.ngOnInit();
  });

  describe('when add event form is submitted', () => {
    it('should dispatch addEventStart action', () => {
      const countryDTO: CountryDTO = { name: 'country' };
      const provinceDTO: ProvinceDTO = {
        name: 'province',
        country: countryDTO,
      };
      const cityDTO: CityDTO = {
        name: 'city',
        latitude: 10,
        longitude: 20,
        province: provinceDTO,
      };
      const victimDTO: VictimDTO = {
        totalNumberOfFatalities: 10,
        numberOfPerpetratorFatalities: 1,
        totalNumberOfInjured: 12,
        numberOfPerpetratorInjured: 2,
        valueOfPropertyDamage: 15000,
      };
      const targetDTO: TargetDTO = {
        target: 'target',
        countryOfOrigin: countryDTO,
      };
      const date = new Date();
      const dateString =
        date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate();

      const eventDTO: EventDTO = {
        summary: 'summary',
        motive: 'motive',
        date: dateString,
        isPartOfMultipleIncidents: true,
        isSuccessful: false,
        isSuicidal: true,
        target: targetDTO,
        city: cityDTO,
        victim: victimDTO,
      };

      component.eventForm
        .get('event')
        .setValue({ ...eventDTO, date: new Date() });
      component.eventForm.get('target').setValue(targetDTO);
      component.eventForm.get('city').setValue(cityDTO);
      component.eventForm.get('victim').setValue(victimDTO);
      component.eventForm.get('province').setValue(provinceDTO);
      component.eventForm.get('country').setValue(countryDTO);

      expect(component.eventForm.valid).toBeTruthy();

      component.onSubmitForm();

      expect(store.dispatch).toHaveBeenCalledWith(
        EventActions.addEventStart({
          event: eventDTO,
        })
      );
    });
  });
});
