import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Store, StoreModule } from '@ngrx/store';
import * as L from 'leaflet';
import { of } from 'rxjs';
import {
  AuthStoreState,
  selectAuthState,
} from 'src/app/auth/store/auth.reducer';
import { MaterialModule } from 'src/app/common/material.module';
import {
  selectAllEventsInRadius,
  selectLastDeletedEvent,
  selectMaxRadiusOfEventsDetection,
} from 'src/app/event/store/event.reducer';
import AppStoreState from 'src/app/store/app.state';

import * as AuthActions from '../../auth/store/auth.actions';
import * as EventActions from '../../event/store/event.actions';
import MarkerService from '../services/marker.service';
import ShapeService from '../services/shape.service';
import { MapComponent } from './map.component';

describe('MapComponent', () => {
  let component: MapComponent;
  let fixture: ComponentFixture<MapComponent>;
  let store: Store<AppStoreState>;
  let markerService: MarkerService;
  let shapeService: ShapeService;

  const userMarkerPosition: L.LatLngExpression = [20, 10];
  const stateWithUser: AuthStoreState = {
    user: { token: 'token', expirationDate: new Date(Date.now() + 36000000) },
    authErrorMessages: [],
    isLoading: false,
    userLocation: userMarkerPosition,
  };
  const maxRadiusOfEventsDetection = 4000000;
  const userMarker: L.Marker = L.marker(userMarkerPosition);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MapComponent],
      imports: [
        StoreModule.forRoot({}),
        HttpClientTestingModule,
        MaterialModule,
        BrowserAnimationsModule,
      ],
      providers: [Store],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MapComponent);
    component = fixture.componentInstance;

    store = TestBed.inject(Store);
    markerService = TestBed.inject(MarkerService);
    shapeService = TestBed.inject(ShapeService);

    spyOn(store, 'dispatch');
    spyOn(markerService, 'cleanMapFromCircleMarkers').and.callThrough();
    spyOn(markerService, 'removeCircleMarkerByCity').and.callThrough();
    spyOn(markerService, 'createCircleMarkersFromEvents').and.callThrough();
    spyOn(markerService, 'createCircleMarker').and.callThrough();
    spyOn(markerService, 'removeCircleMarker').and.callThrough();
    spyOn(markerService, 'removeMarker').and.callThrough();
    spyOn(markerService, 'createUserPositionMarker').and.returnValue(
      userMarker
    );
    spyOn(shapeService, 'getCountriesShapes').and.callThrough();
  });

  describe('when initialize component', () => {
    it('and user is logged in should fetch events', () => {
      spyOn(store, 'select').and.callFake((selector) => {
        if (selector === selectAllEventsInRadius) {
          return of([]);
        } else if (selector === selectAuthState) {
          return of(stateWithUser);
        } else if (selector === selectLastDeletedEvent) {
          return of();
        } else if (selector === selectMaxRadiusOfEventsDetection) {
          return of(maxRadiusOfEventsDetection);
        }
      });

      fixture.detectChanges();
      component.ngOnInit();

      expect(store.select).toHaveBeenCalled();
      expect(store.dispatch).toHaveBeenCalledWith(EventActions.fetchEvents());
      expect(markerService.removeMarker).toHaveBeenCalled();
      expect(markerService.createUserPositionMarker).toHaveBeenCalled();
      expect(markerService.removeCircleMarker).toHaveBeenCalled();
      expect(markerService.createCircleMarker).toHaveBeenCalled();
      expect(shapeService.getCountriesShapes).toHaveBeenCalled();
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
        if (selector === selectAllEventsInRadius) {
          return of([event1, event2]);
        } else if (selector === selectAuthState) {
          return of(stateWithUser);
        } else if (selector === selectLastDeletedEvent) {
          return of();
        } else if (selector === selectMaxRadiusOfEventsDetection) {
          return of(maxRadiusOfEventsDetection);
        }
      });

      fixture.detectChanges();
      component.ngOnInit();

      expect(store.select).toHaveBeenCalled();
      expect(store.dispatch).toHaveBeenCalledWith(EventActions.fetchEvents());
      expect(markerService.removeMarker).toHaveBeenCalled();
      expect(markerService.createUserPositionMarker).toHaveBeenCalled();
      expect(markerService.removeCircleMarker).toHaveBeenCalled();
      expect(markerService.createCircleMarker).toHaveBeenCalled();
      expect(markerService.cleanMapFromCircleMarkers).toHaveBeenCalled();
      expect(markerService.createCircleMarkersFromEvents).toHaveBeenCalled();
      expect(shapeService.getCountriesShapes).toHaveBeenCalled();
    });

    it('and events are not fetched should remove markers', () => {
      spyOn(store, 'select').and.callFake((selector) => {
        if (selector === selectAllEventsInRadius) {
          return of([]);
        } else if (selector === selectAuthState) {
          return of(stateWithUser);
        } else if (selector === selectLastDeletedEvent) {
          return of();
        } else if (selector === selectMaxRadiusOfEventsDetection) {
          return of(maxRadiusOfEventsDetection);
        }
      });

      fixture.detectChanges();
      component.ngOnInit();

      expect(store.select).toHaveBeenCalled();
      expect(store.dispatch).toHaveBeenCalledWith(EventActions.fetchEvents());
      expect(markerService.removeMarker).toHaveBeenCalled();
      expect(markerService.createUserPositionMarker).toHaveBeenCalled();
      expect(markerService.removeCircleMarker).toHaveBeenCalled();
      expect(markerService.createCircleMarker).toHaveBeenCalled();
      expect(shapeService.getCountriesShapes).toHaveBeenCalled();
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
          numberOfPerpetratorsFatalities: 3,
          totalNumberOfInjured: 14,
          numberOfPerpetratorsInjured: 4,
          valueOfPropertyDamage: 2000,
        },
      };

      spyOn(store, 'select').and.callFake((selector) => {
        if (selector === selectAllEventsInRadius) {
          return of([]);
        } else if (selector === selectAuthState) {
          return of(stateWithUser);
        } else if (selector === selectLastDeletedEvent) {
          return of(event);
        } else if (selector === selectMaxRadiusOfEventsDetection) {
          return of(maxRadiusOfEventsDetection);
        }
      });

      fixture.detectChanges();
      component.ngOnInit();

      expect(store.select).toHaveBeenCalled();
      expect(store.dispatch).toHaveBeenCalledWith(EventActions.fetchEvents());
      expect(markerService.removeMarker).toHaveBeenCalled();
      expect(markerService.createUserPositionMarker).toHaveBeenCalled();
      expect(markerService.removeCircleMarker).toHaveBeenCalled();
      expect(markerService.createCircleMarker).toHaveBeenCalled();
      expect(markerService.removeCircleMarkerByCity).toHaveBeenCalled();
      expect(shapeService.getCountriesShapes).toHaveBeenCalled();
    });

    it('and user location is setted should create circle marker', () => {
      const latitude = 10;
      const longitude = 20;
      const position: Position = {
        coords: {
          latitude,
          longitude,
          accuracy: 10,
          altitude: 100,
          altitudeAccuracy: 10,
          heading: 2,
          speed: 10,
        },
        timestamp: +new Date(),
      };
      const userLocation: L.LatLngExpression = [latitude, longitude];

      spyOn(store, 'select').and.callFake((selector) => {
        if (selector === selectAllEventsInRadius) {
          return of([]);
        } else if (selector === selectAuthState) {
          return of(stateWithUser);
        } else if (selector === selectLastDeletedEvent) {
          return of();
        } else if (selector === selectMaxRadiusOfEventsDetection) {
          return of(maxRadiusOfEventsDetection);
        }
      });

      spyOn(navigator.geolocation, 'getCurrentPosition').and.callFake(
        function (): void {
          arguments[0](position);
        }
      );

      fixture.detectChanges();
      component.ngOnInit();

      expect(store.dispatch).toHaveBeenCalledWith(
        AuthActions.setUserLocation({ userLocation })
      );
      expect(store.select).toHaveBeenCalled();
      expect(store.dispatch).toHaveBeenCalledWith(EventActions.fetchEvents());
      expect(markerService.removeMarker).toHaveBeenCalled();
      expect(markerService.createUserPositionMarker).toHaveBeenCalled();
      expect(markerService.removeCircleMarker).toHaveBeenCalled();
      expect(markerService.createCircleMarker).toHaveBeenCalled();
      expect(shapeService.getCountriesShapes).toHaveBeenCalled();
    });
  });
});
