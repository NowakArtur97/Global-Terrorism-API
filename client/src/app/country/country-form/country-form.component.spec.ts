import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Store, StoreModule } from '@ngrx/store';
import { of } from 'rxjs';
import { MaterialModule } from 'src/app/common/material.module';
import { EventStoreState, selectEventToUpdate } from 'src/app/event/store/event.reducer';
import AppStoreState from 'src/app/store/app.state';

import { CountryFormComponent } from './country-form.component';

describe('CountryFormComponent', () => {
  let component: CountryFormComponent;
  let fixture: ComponentFixture<CountryFormComponent>;
  let store: Store<AppStoreState>;
  const initialState: EventStoreState = {
    ids: [],
    entities: {},
    eventToUpdate: null,
    lastUpdatedEvent: null,
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CountryFormComponent],
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
    fixture = TestBed.createComponent(CountryFormComponent);
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
  });
});
