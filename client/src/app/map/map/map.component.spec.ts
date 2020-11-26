import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Store, StoreModule } from '@ngrx/store';
import { of } from 'rxjs';
import { AuthStoreState } from 'src/app/auth/store/auth.reducer';
import { MaterialModule } from 'src/app/common/material.module';
import {
  selectAllEvents,
  selectAllEventsBeforeDate,
  selectLastDeletedEvent,
} from 'src/app/event/store/event.reducer';
import AppStoreState from 'src/app/store/app.state';

import * as EventActions from '../../event/store/event.actions';
import MarkerService from '../marker.service';
import { MapComponent } from './map.component';

describe('MapComponent', () => {
  let component: MapComponent;
  let fixture: ComponentFixture<MapComponent>;
  let store: Store<AppStoreState>;
  let markerService: MarkerService;

  const stateWithUser: AuthStoreState = {
    user: { token: 'token', expirationDate: new Date(Date.now() + 36000000) },
    authErrorMessages: [],
    isLoading: false,
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MapComponent],
      imports: [
        StoreModule.forRoot({}),
        MaterialModule,
        BrowserAnimationsModule,
      ],
      providers: [
        Store,
        {
          provide: MarkerService,
          useValue: jasmine.createSpyObj('markerService', [
            'cleanMapFromMarkers',
            'removeMarker',
            'createCircleMarkers',
          ]),
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MapComponent);
    component = fixture.componentInstance;

    store = TestBed.inject(Store);
    markerService = TestBed.inject(MarkerService);

    spyOn(store, 'dispatch');
  });

  describe('when initialize component', () => {
    it('and user is logged in should fetch events', () => {
      spyOn(store, 'select').and.callFake((selector) => {
        if (selector === selectAllEvents) {
          return of([]);
        } else if (selector === 'auth') {
          return of(stateWithUser);
        } else if (selector === selectLastDeletedEvent) {
          return of();
        }
      });

      fixture.detectChanges();
      component.ngOnInit();

      expect(store.select).toHaveBeenCalled();
      expect(store.dispatch).toHaveBeenCalledWith(EventActions.fetchEvents());
    });

    it('and events are fetched should show markers', () => {
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
          numberOfPerpetratorFatalities: 3,
          totalNumberOfInjured: 14,
          numberOfPerpetratorInjured: 4,
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
          numberOfPerpetratorFatalities: 2,
          totalNumberOfInjured: 11,
          numberOfPerpetratorInjured: 6,
          valueOfPropertyDamage: 7000,
        },
      };

      spyOn(store, 'select').and.callFake((selector) => {
        if (selector === selectAllEventsBeforeDate) {
          return of([event1, event2]);
        } else if (selector === 'auth') {
          return of(stateWithUser);
        } else if (selector === selectLastDeletedEvent) {
          return of();
        }
      });

      fixture.detectChanges();
      component.ngOnInit();

      expect(store.select).toHaveBeenCalled();
      expect(store.dispatch).toHaveBeenCalledWith(EventActions.fetchEvents());
      expect(markerService.createCircleMarkers).toHaveBeenCalled();
    });

    it('and events are not fetched should remove markers', () => {
      spyOn(store, 'select').and.callFake((selector) => {
        if (selector === selectAllEvents) {
          return of([]);
        } else if (selector === 'auth') {
          return of(stateWithUser);
        } else if (selector === selectLastDeletedEvent) {
          return of();
        }
      });

      fixture.detectChanges();
      component.ngOnInit();

      expect(store.select).toHaveBeenCalled();
      expect(store.dispatch).toHaveBeenCalledWith(EventActions.fetchEvents());
      expect(markerService.cleanMapFromMarkers).toHaveBeenCalled();
    });

    it('and in store is deleted event should remove marker', () => {
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
          numberOfPerpetratorFatalities: 3,
          totalNumberOfInjured: 14,
          numberOfPerpetratorInjured: 4,
          valueOfPropertyDamage: 2000,
        },
      };

      spyOn(store, 'select').and.callFake((selector) => {
        if (selector === selectAllEvents) {
          return of([]);
        } else if (selector === 'auth') {
          return of(stateWithUser);
        } else if (selector === selectLastDeletedEvent) {
          return of(event);
        }
      });

      fixture.detectChanges();
      component.ngOnInit();

      expect(store.select).toHaveBeenCalled();
      expect(store.dispatch).toHaveBeenCalledWith(EventActions.fetchEvents());
      expect(markerService.removeMarker).toHaveBeenCalled();
    });
  });
});
