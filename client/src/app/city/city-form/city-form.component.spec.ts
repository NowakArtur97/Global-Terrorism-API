import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Store, StoreModule } from '@ngrx/store';
import { of } from 'rxjs';
import { MaterialModule } from 'src/app/common/material.module';
import { EventStoreState, selectEventToUpdate } from 'src/app/event/store/event.reducer';
import AppStoreState from 'src/app/store/app.state';

import { CityFormComponent } from './city-form.component';

describe('CityFormComponent', () => {
  let component: CityFormComponent;
  let fixture: ComponentFixture<CityFormComponent>;
  let store: Store<AppStoreState>;
  const initialState: EventStoreState = {
    ids: [],
    entities: {},
    eventToUpdate: null,
    lastUpdatedEvent: null,
    isLoading: false,
  };

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

    spyOn(store, 'select').and.callFake((selector) => {
      if (selector === selectEventToUpdate) {
        return of(initialState);
      }
    });

    fixture.detectChanges();
    component.ngOnInit();
  });

  describe('form validation', () => {
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
});
