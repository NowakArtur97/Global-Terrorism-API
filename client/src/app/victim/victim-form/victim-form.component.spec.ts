import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Store, StoreModule } from '@ngrx/store';
import { of } from 'rxjs';
import { MaterialModule } from 'src/app/common/material.module';
import { EventStoreState, selectEventToUpdate } from 'src/app/event/store/event.reducer';
import AppStoreState from 'src/app/store/app.state';

import { VictimFormComponent } from './victim-form.component';

describe('VictimFormComponent', () => {
  let component: VictimFormComponent;
  let fixture: ComponentFixture<VictimFormComponent>;
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
  const initialStateWithInvalidEventToUpdate: EventStoreState = {
    ids: [],
    entities: {},
    eventToUpdate: invalidEventToUpdate,
    lastUpdatedEvent: null,
    isLoading: false,
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [VictimFormComponent],
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
    fixture = TestBed.createComponent(VictimFormComponent);
    component = fixture.componentInstance;

    store = TestBed.inject(Store);
  });

  describe('form validation', () => {
    describe('when add event', () => {
      beforeEach(() => {
        spyOn(store, 'select').and.callFake((selector) => {
          if (selector === selectEventToUpdate) {
            return of(initialState.eventToUpdate);
          }
        });

        fixture.detectChanges();
        component.ngOnInit();
      });

      it('with empty total number of fatalities should be invalid', () => {
        component.totalNumberOfFatalities.setValue('');

        const totalNumberOfFatalities = component.totalNumberOfFatalities;
        const errors = totalNumberOfFatalities.errors;
        expect(errors.notBlank).toBeTruthy();
      });

      it('with blank total number of fatalities should be invalid', () => {
        component.totalNumberOfFatalities.setValue('    ');

        const totalNumberOfFatalities = component.totalNumberOfFatalities;
        const errors = totalNumberOfFatalities.errors;
        expect(errors.notBlank).toBeTruthy();
      });

      it('with negative total number of fatalities should be invalid', () => {
        component.totalNumberOfFatalities.setValue(-1);

        const totalNumberOfFatalities = component.totalNumberOfFatalities;
        const errors = totalNumberOfFatalities.errors;
        expect(errors.min).toBeTruthy();
      });

      it('with empty number of perpetrator fatalities should be invalid', () => {
        component.numberOfPerpetratorFatalities.setValue('');

        const numberOfPerpetratorFatalities =
          component.numberOfPerpetratorFatalities;
        const errors = numberOfPerpetratorFatalities.errors;
        expect(errors.notBlank).toBeTruthy();
      });

      it('with blank number of perpetrator fatalities should be invalid', () => {
        component.numberOfPerpetratorFatalities.setValue('    ');

        const numberOfPerpetratorFatalities =
          component.numberOfPerpetratorFatalities;
        const errors = numberOfPerpetratorFatalities.errors;
        expect(errors.notBlank).toBeTruthy();
      });

      it('with negative number of perpetrator fatalities should be invalid', () => {
        component.numberOfPerpetratorFatalities.setValue(-1);

        const numberOfPerpetratorFatalities =
          component.numberOfPerpetratorFatalities;
        const errors = numberOfPerpetratorFatalities.errors;
        expect(errors.min).toBeTruthy();
      });

      it('with number of perpetrator fatalities bigger than total value should be invalid', () => {
        component.totalNumberOfFatalities.setValue(10);
        component.numberOfPerpetratorFatalities.setValue(20);

        const numberOfPerpetratorFatalities =
          component.numberOfPerpetratorFatalities;
        const errors = numberOfPerpetratorFatalities.errors;
        expect(errors.lowerOrEqual).toBeTruthy();
      });

      it('with empty total number of injured should be invalid', () => {
        component.totalNumberOfInjured.setValue('');

        const totalNumberOfInjured = component.totalNumberOfInjured;
        const errors = totalNumberOfInjured.errors;
        expect(errors.notBlank).toBeTruthy();
      });

      it('with blank total number of injured should be invalid', () => {
        component.totalNumberOfInjured.setValue('    ');

        const totalNumberOfInjured = component.totalNumberOfInjured;
        const errors = totalNumberOfInjured.errors;
        expect(errors.notBlank).toBeTruthy();
      });

      it('with negative total number of injured should be invalid', () => {
        component.totalNumberOfInjured.setValue(-1);

        const totalNumberOfInjured = component.totalNumberOfInjured;
        const errors = totalNumberOfInjured.errors;
        expect(errors.min).toBeTruthy();
      });

      it('with empty number of perpetrator injured should be invalid', () => {
        component.numberOfPerpetratorInjured.setValue('');

        const numberOfPerpetratorInjured = component.numberOfPerpetratorInjured;
        const errors = numberOfPerpetratorInjured.errors;
        expect(errors.notBlank).toBeTruthy();
      });

      it('with blank number of perpetrator injured should be invalid', () => {
        component.numberOfPerpetratorInjured.setValue('    ');

        const numberOfPerpetratorInjured = component.numberOfPerpetratorInjured;
        const errors = numberOfPerpetratorInjured.errors;
        expect(errors.notBlank).toBeTruthy();
      });

      it('with negative number of perpetrator injured should be invalid', () => {
        component.numberOfPerpetratorInjured.setValue(-1);

        const numberOfPerpetratorInjured = component.numberOfPerpetratorInjured;
        const errors = numberOfPerpetratorInjured.errors;
        expect(errors.min).toBeTruthy();
      });

      it('with number of perpetrator injured bigger than total value should be invalid', () => {
        component.totalNumberOfInjured.setValue(10);
        component.numberOfPerpetratorInjured.setValue(20);

        const numberOfPerpetratorInjured = component.numberOfPerpetratorInjured;
        const errors = numberOfPerpetratorInjured.errors;
        expect(errors.lowerOrEqual).toBeTruthy();
      });

      it('with empty value of property damage should be invalid', () => {
        component.valueOfPropertyDamage.setValue('');

        const valueOfPropertyDamage = component.valueOfPropertyDamage;
        const errors = valueOfPropertyDamage.errors;
        expect(errors.notBlank).toBeTruthy();
      });

      it('with blank value of property damage should be invalid', () => {
        component.valueOfPropertyDamage.setValue('    ');

        const valueOfPropertyDamage = component.valueOfPropertyDamage;
        const errors = valueOfPropertyDamage.errors;
        expect(errors.notBlank).toBeTruthy();
      });

      it('with negative value of property damage should be invalid', () => {
        component.valueOfPropertyDamage.setValue(-1);

        const valueOfPropertyDamage = component.valueOfPropertyDamage;
        const errors = valueOfPropertyDamage.errors;
        expect(errors.min).toBeTruthy();
      });
    });

    describe('when update', () => {
      it('valid event should be valid', () => {
        spyOn(store, 'select').and.callFake((selector) => {
          if (selector === selectEventToUpdate) {
            return of(initialStateWithEventToUpdate.eventToUpdate);
          }
        });

        fixture.detectChanges();
        component.ngOnInit();

        expect(component.formGroup.valid).toBeTruthy();
        expect(component.totalNumberOfFatalities.value).toBe(
          eventToUpdate.victim.totalNumberOfFatalities
        );
        expect(component.numberOfPerpetratorFatalities.value).toBe(
          eventToUpdate.victim.numberOfPerpetratorFatalities
        );
        expect(component.totalNumberOfInjured.value).toBe(
          eventToUpdate.victim.totalNumberOfInjured
        );
        expect(component.numberOfPerpetratorInjured.value).toBe(
          eventToUpdate.victim.numberOfPerpetratorInjured
        );
        expect(component.valueOfPropertyDamage.value).toBe(
          eventToUpdate.victim.valueOfPropertyDamage
        );
      });

      it('invalid event should be invalid', () => {
        spyOn(store, 'select').and.callFake((selector) => {
          if (selector === selectEventToUpdate) {
            return of(initialStateWithInvalidEventToUpdate.eventToUpdate);
          }
        });

        fixture.detectChanges();
        component.ngOnInit();

        expect(component.formGroup.valid).toBeFalsy();
        expect(component.totalNumberOfFatalities.valid).toBeFalsy();
        expect(component.numberOfPerpetratorFatalities.valid).toBeFalsy();
        expect(component.totalNumberOfInjured.valid).toBeFalsy();
        expect(component.numberOfPerpetratorInjured.valid).toBeFalsy();
        expect(component.valueOfPropertyDamage.valid).toBeFalsy();
      });
    });
  });
});
