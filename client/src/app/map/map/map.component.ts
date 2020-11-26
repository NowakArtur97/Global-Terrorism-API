import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import * as L from 'leaflet';
import { icon } from 'leaflet';
import { Subscription } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import User from '../../auth/models/user.model';
import Event from '../../event/models/event.model';

import MarkerService from './../marker.service';
import * as EventActions from '../../event/store/event.actions';
import {
  selectAllEvents,
  selectAllEventsBeforeDate,
  selectLastDeletedEvent,
} from 'src/app/event/store/event.reducer';
import AppStoreState from 'src/app/store/app.state';
@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css'],
})
export class MapComponent implements OnInit, OnDestroy, AfterViewInit {
  private map: L.Map;
  private markers: L.CircleMarker[] = [];
  private eventsSubscription$: Subscription;
  private userSubscription$: Subscription;
  private deleteEventSubscription$: Subscription;

  events: Event[] = [];

  icon = icon({
    iconSize: [25, 41],
    iconAnchor: [13, 41],
    iconUrl: 'assets/leaflet/marker-icon.png',
    shadowUrl: 'assets/leaflet/marker-shadow.png',
  });

  constructor(
    private store: Store<AppStoreState>,
    private markerService: MarkerService
  ) {}

  ngOnInit(): void {
    this.userSubscription$ = this.store
      .select('auth')
      .pipe(map((authState) => authState.user))
      .subscribe((user: User) => {
        if (user) {
          this.store.dispatch(EventActions.fetchEvents());
        }
      });

    this.eventsSubscription$ = this.store
      .select(selectAllEventsBeforeDate)
      .pipe(
        tap((events) => {
          if (this.map && events && events.length === 0) {
            this.markerService.cleanMapFromMarkers(this.map, this.markers);
          }
        })
      )
      .subscribe((events: Event[]) => {
        this.events = events;
        if (this.map && this.events && this.markers?.length === 0) {
          this.showMarkers();
        } else if (this.map && events && events.length !== 0) {
          this.markerService.cleanMapFromMarkers(this.map, this.markers);
          this.showMarkers();
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

  ngOnDestroy(): void {
    this.eventsSubscription$?.unsubscribe();
    this.userSubscription$?.unsubscribe();
    this.deleteEventSubscription$?.unsubscribe();
  }

  ngAfterViewInit(): void {
    this.initMap();
  }

  private initMap(): void {
    this.map = L.map('map', {
      center: [39.8282, -98.5795],
      zoom: 3,
    });

    const tiles = L.tileLayer(
      'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
      {
        maxZoom: 19,
        attribution:
          '© <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
      }
    );
    tiles.addTo(this.map);
  }

  private showMarkers(): void {
    this.markers = this.markerService.createCircleMarkers(
      this.events,
      this.map
    );
  }
}
