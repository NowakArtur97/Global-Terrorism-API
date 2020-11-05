import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { EffectsModule } from '@ngrx/effects';
import { Store, StoreModule } from '@ngrx/store';
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
import { EventFormWrapperComponent } from './event-form-wrapper.component';

describe('EventFormWrapperComponent', () => {
  let component: EventFormWrapperComponent;
  let fixture: ComponentFixture<EventFormWrapperComponent>;
  let store: Store<AppStoreState>;

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
      const eventDTO: EventDTO = {
        summary: 'summary',
        motive: 'motive',
        date: new Date(),
        isPartOfMultipleIncidents: true,
        isSuccessful: false,
        isSuicidal: true,
        target: targetDTO,
        city: cityDTO,
        victim: victimDTO,
      };

      component.eventForm.get('event').setValue(eventDTO);
      component.eventForm.get('target').setValue(targetDTO);
      component.eventForm.get('city').setValue(cityDTO);
      component.eventForm.get('victim').setValue(victimDTO);
      component.eventForm.get('province').setValue(provinceDTO);
      component.eventForm.get('country').setValue(countryDTO);

      expect(component.eventForm.valid).toBeTruthy();

      component.onAddEvent();

      expect(store.dispatch).toHaveBeenCalledWith(
        EventActions.addEventStart({
          event: eventDTO,
        })
      );
    });
  });
});
