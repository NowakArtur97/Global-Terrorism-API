import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import * as L from 'leaflet';
import { icon } from 'leaflet';
import { Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import User from '../../auth/models/user.model';
import Event from '../../event/models/event.model';

import MarkerService from './../marker.service';
import * as EventActions from '../../event/store/event.actions';
import * as AuthActions from '../../auth/store/auth.actions';
import {
  selectAllEventsInRadius,
  selectLastDeletedEvent,
  selectMaxRadiusOfEventsDetection,
} from 'src/app/event/store/event.reducer';
import AppStoreState from 'src/app/store/app.state';
import { selectAuthState } from 'src/app/auth/store/auth.reducer';
@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css'],
})
export class MapComponent implements OnInit, OnDestroy, AfterViewInit {
  private readonly ZOOM = 3;
  private readonly MAX_ZOOM = 19;
  private readonly ICO_SIZE: L.PointExpression = [25, 41];
  private readonly ICON_ANCHOR: L.PointExpression = [13, 41];
  private readonly ICON_URL = 'assets/leaflet/marker-icon.png';
  private readonly TILE_LAYER =
    'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
  private readonly TILES_ATRIBUTION =
    '© <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>';

  private map: L.Map;
  private markers: L.CircleMarker[] = [];
  private eventsSubscription$: Subscription;
  private eventsRadiusSubscription$: Subscription;
  private userSubscription$: Subscription;
  private deleteEventSubscription$: Subscription;
  private userLocation: L.LatLngExpression = [40, -100];

  isUserLoggedIn = false;
  events: Event[] = [];
  icon = icon({
    iconSize: this.ICO_SIZE,
    iconAnchor: this.ICON_ANCHOR,
    iconUrl: this.ICON_URL,
  });
  eventsRadiusMarker: L.CircleMarker<any>;

  constructor(
    private store: Store<AppStoreState>,
    private markerService: MarkerService
  ) {}

  ngOnInit(): void {
    this.getUserLocation();
    this.setupSubscriptions();
  }

  ngOnDestroy(): void {
    this.eventsSubscription$?.unsubscribe();
    this.eventsRadiusSubscription$?.unsubscribe();
    this.userSubscription$?.unsubscribe();
    this.deleteEventSubscription$?.unsubscribe();
  }

  ngAfterViewInit(): void {
    this.initMap();
  }

  private setupSubscriptions(): void {
    this.userSubscription$ = this.store
      .select(selectAuthState)
      .pipe(map((authState) => authState.user))
      .subscribe((user: User) => {
        if (user) {
          this.isUserLoggedIn = true;
          this.store.dispatch(EventActions.fetchEvents());
        } else {
          this.isUserLoggedIn = false;
        }
      });

    this.eventsSubscription$ = this.store
      .select(selectAllEventsInRadius)
      .subscribe((events: Event[]) => {
        this.events = events;
        if (this.map && this.events) {
          this.markerService.cleanMapFromMarkers(this.map, this.markers);
          this.markers = this.markerService.createCircleMarkersFromEvents(
            this.events,
            this.map
          );
        }
      });

    this.eventsRadiusSubscription$ = this.store
      .select(selectMaxRadiusOfEventsDetection)
      .subscribe((maxRadiusOfEventsDetection) => {
        if (this.map && maxRadiusOfEventsDetection > 0) {
          this.markerService.removeCircleMarker(
            this.eventsRadiusMarker,
            this.map
          );
          this.eventsRadiusMarker = this.markerService.createCircleMarker(
            this.userLocation,
            maxRadiusOfEventsDetection,
            this.map
          );
        }
      });

    this.deleteEventSubscription$ = this.store
      .select(selectLastDeletedEvent)
      .subscribe((lastDeletedEvent) => {
        if (lastDeletedEvent) {
          this.markerService.removeMarker(
            this.map,
            this.markers,
            lastDeletedEvent.city
          );
        }
      });
  }

  private initMap(): void {
    this.map = L.map('map').setView(this.userLocation, this.ZOOM);
    const tiles = L.tileLayer(this.TILE_LAYER, {
      maxZoom: this.MAX_ZOOM,
      attribution: this.TILES_ATRIBUTION,
    });
    tiles.addTo(this.map);
  }

  private getUserLocation(): void {
    if (!navigator.geolocation) {
      console.log('Location is not supported');
      return;
    }
    navigator.geolocation.getCurrentPosition((position) => {
      const coords = position.coords;
      this.userLocation = [coords.latitude, coords.longitude];
      this.markerService.createUserPositionMarker(this.userLocation, this.map);
      this.store.dispatch(
        AuthActions.setUserLocation({ userLocation: this.userLocation })
      );
    });
  }
}
