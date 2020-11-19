import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Store, StoreModule } from '@ngrx/store';
import { of } from 'rxjs';
import { MaterialModule } from 'src/app/common/material.module';
import AppStoreState from 'src/app/store/app.state';

import { EventStoreState, selectEventToUpdate } from '../store/event.reducer';
import { EventFormComponent } from './event-form.component';

describe('EventFormComponent', () => {
  let component: EventFormComponent;
  let fixture: ComponentFixture<EventFormComponent>;
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
  const state: EventStoreState = {
    ids: [],
    entities: {},
    eventToUpdate: null,
    lastUpdatedEvent: null,
    isLoading: false,
  };
  const stateWithEventToUpdate: EventStoreState = {
    ids: [],
    entities: {},
    eventToUpdate,
    lastUpdatedEvent: null,
    isLoading: false,
  };
  const stateWithInvalidEventToUpdate: EventStoreState = {
    ids: [],
    entities: {},
    eventToUpdate: invalidEventToUpdate,
    lastUpdatedEvent: null,
    isLoading: false,
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EventFormComponent],
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
    fixture = TestBed.createComponent(EventFormComponent);
    component = fixture.componentInstance;

    store = TestBed.inject(Store);
  });

  describe('form validation', () => {
    describe('when add event', () => {
      beforeEach(() => {
        spyOn(store, 'select').and.callFake((selector) => {
          if (selector === selectEventToUpdate) {
            return of(state.eventToUpdate);
          }
        });

        fixture.detectChanges();
        component.ngOnInit();
      });

      it('which is valid should be valid', () => {
        component.summary.setValue('summary');
        component.motive.setValue('motive');
        component.date.setValue(new Date());
        component.isPartOfMultipleIncidents.setValue('true');
        component.isSuccessful.setValue('true');
        component.isSuicidal.setValue('true');

        expect(component.formGroup.valid).toBeTruthy();
        expect(component.summary.valid).toBeTruthy();
        expect(component.motive.valid).toBeTruthy();
        expect(component.date.valid).toBeTruthy();
        expect(component.isPartOfMultipleIncidents.valid).toBeTruthy();
        expect(component.isSuccessful.valid).toBeTruthy();
        expect(component.isSuicidal.valid).toBeTruthy();
      });

      it('with empty summary should be invalid', () => {
        component.summary.setValue('');

        const summary = component.summary;
        const errors = summary.errors;
        expect(errors.notBlank).toBeTruthy();
      });

      it('with blank summary should be invalid', () => {
        component.summary.setValue('    ');

        const summary = component.summary;
        const errors = summary.errors;
        expect(errors.notBlank).toBeTruthy();
      });

      it('with empty motive should be invalid', () => {
        component.motive.setValue('');

        const motive = component.motive;
        const errors = motive.errors;
        expect(errors.notBlank).toBeTruthy();
      });

      it('with blank motive should be invalid', () => {
        component.motive.setValue('    ');

        const motive = component.motive;
        const errors = motive.errors;
        expect(errors.notBlank).toBeTruthy();
      });

      it('with empty date should be invalid', () => {
        component.date.setValue('');

        const date = component.date;
        const errors = date.errors;
        expect(errors.notBlank).toBeTruthy();
      });

      it('with blank date should be invalid', () => {
        component.date.setValue('    ');

        const date = component.date;
        const errors = date.errors;
        expect(errors.notBlank).toBeTruthy();
      });

      it('with date in the future should be invalid', () => {
        component.date.setValue(new Date(2100, 1, 1));

        const date = component.date;
        const errors = date.errors;
        expect(errors.dateInPast).toBeTruthy();
      });

      it('with empty is part of multiple incidents option should be invalid', () => {
        component.isPartOfMultipleIncidents.setValue('');

        const isPartOfMultipleIncidents = component.isPartOfMultipleIncidents;
        const errors = isPartOfMultipleIncidents.errors;
        expect(errors.notBlank).toBeTruthy();
      });

      it('with blank is part of multiple incidents option should be invalid', () => {
        component.isPartOfMultipleIncidents.setValue('    ');

        const isPartOfMultipleIncidents = component.isPartOfMultipleIncidents;
        const errors = isPartOfMultipleIncidents.errors;
        expect(errors.notBlank).toBeTruthy();
      });

      it('with empty is successful option should be invalid', () => {
        component.isSuccessful.setValue('');

        const isSuccessful = component.isSuccessful;
        const errors = isSuccessful.errors;
        expect(errors.notBlank).toBeTruthy();
      });

      it('with blank is successful option should be invalid', () => {
        component.isSuccessful.setValue('    ');

        const isSuccessful = component.isSuccessful;
        const errors = isSuccessful.errors;
        expect(errors.notBlank).toBeTruthy();
      });

      it('with empty is suicidal option should be invalid', () => {
        component.isSuicidal.setValue('');

        const isSuicidal = component.isSuicidal;
        const errors = isSuicidal.errors;
        expect(errors.notBlank).toBeTruthy();
      });

      it('with blank is suicidal option should be invalid', () => {
        component.isSuicidal.setValue('    ');

        const isSuicidal = component.isSuicidal;
        const errors = isSuicidal.errors;
        expect(errors.notBlank).toBeTruthy();
      });
    });

    describe('when update', () => {
      it('valid event should be valid', () => {
        spyOn(store, 'select').and.callFake((selector) => {
          if (selector === selectEventToUpdate) {
            return of(stateWithEventToUpdate.eventToUpdate);
          }
        });

        fixture.detectChanges();
        component.ngOnInit();

        expect(component.formGroup.valid).toBeTruthy();
        expect(component.summary.value).toBe(eventToUpdate.summary);
        expect(component.motive.value).toBe(eventToUpdate.motive);
        expect(component.date.value).toEqual(eventToUpdate.date);
        expect(component.isPartOfMultipleIncidents.value).toBe(
          eventToUpdate.isPartOfMultipleIncidents + ''
        );
        expect(component.isSuccessful.value).toBe(
          eventToUpdate.isSuccessful + ''
        );
        expect(component.isSuicidal.value).toBe(eventToUpdate.isSuicidal + '');
      });

      it('invalid event should be invalid', () => {
        spyOn(store, 'select').and.callFake((selector) => {
          if (selector === selectEventToUpdate) {
            return of(stateWithInvalidEventToUpdate.eventToUpdate);
          }
        });

        fixture.detectChanges();
        component.ngOnInit();

        expect(component.formGroup.valid).toBeFalsy();
        expect(component.summary.valid).toBeFalsy();
        expect(component.motive.valid).toBeFalsy();
        expect(component.date.valid).toBeFalsy();
        expect(component.isPartOfMultipleIncidents.valid).toBeTruthy();
        expect(component.isSuccessful.valid).toBeTruthy();
        expect(component.isSuicidal.valid).toBeTruthy();
      });
    });
  });
});
