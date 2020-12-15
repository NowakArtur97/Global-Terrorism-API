import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Store, StoreModule } from '@ngrx/store';
import { of } from 'rxjs';
import { MaterialModule } from 'src/app/common/material.module';
import AppStoreState from 'src/app/store/app.state';

import { selectAllEvents } from '../store/event.reducer';
import { EventListComponent } from './event-list.component';

describe('EventListComponent', () => {
  let component: EventListComponent;
  let fixture: ComponentFixture<EventListComponent>;
  let store: Store<AppStoreState>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EventListComponent],
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
    fixture = TestBed.createComponent(EventListComponent);
    component = fixture.componentInstance;

    store = TestBed.inject(Store);

    const event1 = {
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
        numberOfPerpetratorsFatalities: 3,
        totalNumberOfInjured: 14,
        numberOfPerpetratorsInjured: 4,
        valueOfPropertyDamage: 2000,
      },
    };
    const event2 = {
      id: 12,
      summary: 'summary 2',
      motive: 'motive 2',
      date: new Date(),
      isPartOfMultipleIncidents: true,
      isSuccessful: false,
      isSuicidal: true,
      target: {
        id: 9,
        target: 'target 2',
        countryOfOrigin: { id: 7, name: 'country 2' },
      },
      city: {
        id: 10,
        name: 'city 2',
        latitude: 10,
        longitude: 20,
        province: {
          id: 8,
          name: 'province 2',
          country: { id: 7, name: 'country 2' },
        },
      },
      victim: {
        id: 11,
        totalNumberOfFatalities: 10,
        numberOfPerpetratorsFatalities: 2,
        totalNumberOfInjured: 11,
        numberOfPerpetratorsInjured: 6,
        valueOfPropertyDamage: 7000,
      },
    };

    spyOn(store, 'select').and.callFake((selector) => {
      if (selector === selectAllEvents) {
        return of([event1, event2]);
      }
    });

    fixture.detectChanges();
    component.ngOnInit();
  });

  describe('when initialize component', () => {
    it('should select events from store', () => {
      expect(store.select).toHaveBeenCalled();
    });
  });
});
