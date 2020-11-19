import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Store, StoreModule } from '@ngrx/store';
import { of } from 'rxjs';
import { MaterialModule } from 'src/app/common/material.module';
import {
  EventStoreState,
  selectEventToUpdate,
} from 'src/app/event/store/event.reducer';
import AppStoreState from 'src/app/store/app.state';

import { CityFormComponent } from './city-form.component';

describe('CityFormComponent', () => {
  let component: CityFormComponent;
  let fixture: ComponentFixture<CityFormComponent>;
  let store: Store<AppStoreState>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CityFormComponent],
      imports: [
        StoreModule.forRoot({}),
        ReactiveFormsModule,
        MaterialModule,
        BrowserAnimationsModule,
      ],
      providers: [Store],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CityFormComponent);
    component = fixture.componentInstance;

    store = TestBed.inject(Store);
  });

  describe('form validation', () => {
    describe('when add event', () => {
      beforeEach(() => {
        const state: EventStoreState = {
          ids: [],
          entities: {},
          eventToUpdate: null,
          lastUpdatedEvent: null,
          lastDeletedEvent: null,
          isLoading: false,
        };
        spyOn(store, 'select').and.callFake((selector) => {
          if (selector === selectEventToUpdate) {
            return of(state.eventToUpdate);
          }
        });

        fixture.detectChanges();
        component.ngOnInit();
      });

      it('which is valid should be valid', () => {
        component.name.setValue('city');
        component.latitude.setValue(20);
        component.longitude.setValue(10);

        expect(component.formGroup.valid).toBeTruthy();
        expect(component.name.valid).toBeTruthy();
        expect(component.latitude.valid).toBeTruthy();
        expect(component.longitude.valid).toBeTruthy();
      });

      it('with empty name should be invalid', () => {
        component.name.setValue('');

        const name = component.name;
        const errors = name.errors;
        expect(errors.notBlank).toBeTruthy();
      });

      it('with blank name should be invalid', () => {
        component.name.setValue('    ');

        const name = component.name;
        const errors = name.errors;
        expect(errors.notBlank).toBeTruthy();
      });

      it('with empty latitude should be invalid', () => {
        component.latitude.setValue('');

        const latitude = component.latitude;
        const errors = latitude.errors;
        expect(errors.notBlank).toBeTruthy();
      });

      it('with blank latitude should be invalid', () => {
        component.latitude.setValue('    ');

        const latitude = component.latitude;
        const errors = latitude.errors;
        expect(errors.notBlank).toBeTruthy();
      });

      it('with latitude lesser than -90 should be invalid', () => {
        component.latitude.setValue(-91);

        const latitude = component.latitude;
        const errors = latitude.errors;
        expect(errors.min).toBeTruthy();
      });

      it('with latitude bigger than 90 should be invalid', () => {
        component.latitude.setValue(91);

        const latitude = component.latitude;
        const errors = latitude.errors;
        expect(errors.max).toBeTruthy();
      });

      it('with empty longitude should be invalid', () => {
        component.longitude.setValue('');

        const longitude = component.longitude;
        const errors = longitude.errors;
        expect(errors.notBlank).toBeTruthy();
      });

      it('with blank longitude should be invalid', () => {
        component.longitude.setValue('    ');

        const longitude = component.longitude;
        const errors = longitude.errors;
        expect(errors.notBlank).toBeTruthy();
      });

      it('with longitude lesser than -190 should be invalid', () => {
        component.longitude.setValue(-181);

        const longitude = component.longitude;
        const errors = longitude.errors;
        expect(errors.min).toBeTruthy();
      });

      it('with longitude bigger than 180 should be invalid', () => {
        component.longitude.setValue(181);

        const longitude = component.longitude;
        const errors = longitude.errors;
        expect(errors.max).toBeTruthy();
      });
    });

    describe('when update', () => {
      it('valid event should be valid', () => {
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
        const stateWithEventToUpdate: EventStoreState = {
          ids: [],
          entities: {},
          eventToUpdate,
          lastUpdatedEvent: null,
          lastDeletedEvent: null,
          isLoading: false,
        };
        spyOn(store, 'select').and.callFake((selector) => {
          if (selector === selectEventToUpdate) {
            return of(stateWithEventToUpdate.eventToUpdate);
          }
        });

        fixture.detectChanges();
        component.ngOnInit();

        expect(component.formGroup.valid).toBeTruthy();
        expect(component.name.value).toBe(eventToUpdate.city.name);
        expect(component.latitude.value).toBe(eventToUpdate.city.latitude);
        expect(component.longitude.value).toEqual(eventToUpdate.city.longitude);
      });

      it('invalid event should be invalid', () => {
        const invalidEventToUpdate = {
          id: 6,
          summary: ' ',
          motive: ' ',
          date: new Date(Date.now() + 100000),
          isPartOfMultipleIncidents: null,
          isSuccessful: null,
          isSuicidal: null,
          target: {
            id: 3,
            target: ' ',
            countryOfOrigin: { id: 1, name: ' ' },
          },
          city: {
            id: 4,
            name: ' ',
            latitude: -2000,
            longitude: -1000,
            province: {
              id: 2,
              name: ' ',
              country: { id: 1, name: ' ' },
            },
          },
          victim: {
            id: 5,
            totalNumberOfFatalities: -11,
            numberOfPerpetratorFatalities: -3,
            totalNumberOfInjured: -14,
            numberOfPerpetratorInjured: -4,
            valueOfPropertyDamage: -2000,
          },
        };
        const stateWithInvalidEventToUpdate: EventStoreState = {
          ids: [],
          entities: {},
          eventToUpdate: invalidEventToUpdate,
          lastUpdatedEvent: null,
          lastDeletedEvent: null,
          isLoading: false,
        };
        spyOn(store, 'select').and.callFake((selector) => {
          if (selector === selectEventToUpdate) {
            return of(stateWithInvalidEventToUpdate.eventToUpdate);
          }
        });

        fixture.detectChanges();
        component.ngOnInit();

        expect(component.formGroup.valid).toBeFalsy();
        expect(component.name.valid).toBeFalsy();
        expect(component.latitude.valid).toBeFalsy();
        expect(component.longitude.valid).toBeFalsy();
      });
    });
  });
});
