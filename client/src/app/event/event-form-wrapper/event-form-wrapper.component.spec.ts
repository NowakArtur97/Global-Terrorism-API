import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { EffectsModule } from '@ngrx/effects';
import { Store, StoreModule } from '@ngrx/store';
import { of } from 'rxjs';
import { CityModule } from 'src/app/city/city.module';
import CityDTO from 'src/app/city/models/city.dto';
import { MaterialModule } from 'src/app/common/material.module';
import { CountryModule } from 'src/app/country/country.module';
import CountryDTO from 'src/app/country/models/country.dto';
import ProvinceDTO from 'src/app/province/models/province.dto';
import { ProvinceModule } from 'src/app/province/province.module';
import AppStoreState from 'src/app/store/app.state';
import TargetDTO from 'src/app/target/models/target.dto';
import { TargetModule } from 'src/app/target/target.module';
import VictimDTO from 'src/app/victim/models/victim.dto';
import { VictimModule } from 'src/app/victim/victim.module';

import { EventFormComponent } from '../event-form/event-form.component';
import EventDTO from '../models/event.dto';
import * as EventActions from '../store/event.actions';
import { EventStoreState, selectEventToUpdate } from '../store/event.reducer';
import { EventFormWrapperComponent } from './event-form-wrapper.component';

describe('EventFormWrapperComponent', () => {
  let component: EventFormWrapperComponent;
  let fixture: ComponentFixture<EventFormWrapperComponent>;
  let store: Store<AppStoreState>;
  const eventToUpdate = {
    id: 6,
    summary: 'summary',
    motive: 'motive',
    date: new Date(),
    isPartOfMultipleIncidents: false,
    isSuccessful: true,
    isSuicidal: false,
    target: {
      id: 3,
      target: 'target',
      countryOfOrigin: { id: 1, name: 'country' },
    },
    city: {
      id: 4,
      name: 'city',
      latitude: 20,
      longitude: 10,
      province: {
        id: 2,
        name: 'province',
        country: { id: 1, name: 'country' },
      },
    },
    victim: {
      id: 5,
      totalNumberOfFatalities: 11,
      numberOfPerpetratorFatalities: 3,
      totalNumberOfInjured: 14,
      numberOfPerpetratorInjured: 4,
      valueOfPropertyDamage: 2000,
    },
  };

  const initialState: EventStoreState = {
    ids: [],
    entities: {},
    eventToUpdate: null,
    lastUpdatedEvent: null,
    isLoading: false,
  };

  const initialStateWithEventToUpdate: EventStoreState = {
    ids: [],
    entities: {},
    eventToUpdate,
    lastUpdatedEvent: null,
    isLoading: false,
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
      if (selector === 'event') {
        return of(initialState);
      } else if (selector === selectEventToUpdate) {
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
          eventDTO,
        })
      );
    });
  });
});
