import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Store, StoreModule } from '@ngrx/store';
import { of } from 'rxjs';
import { AuthStoreState } from 'src/app/auth/store/auth.reducer';
import { MaterialModule } from 'src/app/common/material.module';
import AppStoreState from 'src/app/store/app.state';

import * as EventActions from '../../event/store/event.actions';
import EventService from '../services/event.service';
import { selectAllEvents, selectErrorMessages } from '../store/event.reducer';
import { EventListComponent } from './event-list.component';

describe('EventListComponent', () => {
  let component: EventListComponent;
  let fixture: ComponentFixture<EventListComponent>;
  let store: Store<AppStoreState>;
  let eventService: EventService;

  const event = {
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

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EventListComponent],
      imports: [
        StoreModule.forRoot({}),
        HttpClientTestingModule,
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
    eventService = TestBed.inject(EventService);

    const stateWithUser: AuthStoreState = {
      user: {
        token: 'token',
        expirationDate: new Date(Date.now() + 36000000),
      },
      authErrorMessages: [],
      isLoading: false,
      userLocation: [20, 10],
    };

    spyOn(store, 'select').and.callFake((selector) => {
      if (selector === selectAllEvents) {
        return of([event, event2]);
      } else if (selector === selectErrorMessages) {
        return of([]);
      } else if (selector === 'auth') {
        return of(stateWithUser);
      }
    });
    spyOn(store, 'dispatch');

    spyOn(eventService, 'deleteAll').and.callFake(() => of({}));

    component.expandedElement = event;

    fixture.detectChanges();
    component.ngOnInit();
  });

  describe('when initialize component', () => {
    it('should select events from store', () => {
      expect(store.select).toHaveBeenCalled();
    });
  });

  describe('when update event', () => {
    it('should dispatch startFillingOutForm and updateEventStart action', () => {
      component.updateEvent(event);

      expect(store.dispatch).toHaveBeenCalledWith(
        EventActions.startFillingOutForm()
      );
      expect(store.dispatch).toHaveBeenCalledWith(
        EventActions.updateEventStart({ id: event.id })
      );
    });
  });

  describe('when delete event', () => {
    it('should dispatch deleteEventStart action', () => {
      component.deleteEvent(event);

      expect(store.dispatch).toHaveBeenCalledWith(
        EventActions.deleteEventStart({ eventToDelete: event })
      );
    });
  });

  describe('when delete events', () => {
    it('should dispatch deleteEventsStart action', () => {
      component.deleteEvent(event);
      component.selection.select(event, event2);

      component.deleteSelectedEvents();

      expect(store.dispatch).toHaveBeenCalledWith(
        EventActions.deleteEventsStart({ eventsToDelete: [event, event2] })
      );
    });
  });
});
