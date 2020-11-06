// import { HttpClientTestingModule } from '@angular/common/http/testing';
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
  const initialState: EventStoreState = {
    ids: [],
    entities: {},
    eventToUpdate: null,
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

    spyOn(store, 'select').and.callFake((selector) => {
      if (selector === selectEventToUpdate) {
        return of(initialState);
      }
    });

    fixture.detectChanges();
    component.ngOnInit();
  });

  describe('form validation', () => {
    // it('should have default select options', () => {
    //   component.initForm();
    //   expect(component.isPartOfMultipleIncidents.value).toBe('false');
    //   expect(component.isSuccessful.value).toBe('false');
    //   expect(component.isSuicidal.value).toBe('false');
    // });

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
});
