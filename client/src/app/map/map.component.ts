import {
  AfterViewChecked,
  AfterViewInit,
  Component,
  DoCheck,
  OnDestroy,
  OnInit,
} from '@angular/core';
import { Store } from '@ngrx/store';
import * as L from 'leaflet';
import { icon } from 'leaflet';
import { Subscription } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import User from '../auth/models/user.model';
import City from '../city/models/city.model';

import AppStoreState from '../store/app.store.state';
import MarkerService from './marker.service';
import * as CityActions from '../city/store/city.actions';
@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css'],
})
export class MapComponent implements OnInit, OnDestroy, AfterViewInit {
  private map: L.Map;
  private markers: L.Marker[] = [];
  cities: City[] = [];
  citiesSubscription$: Subscription;
  userSubscription$: Subscription;

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
    this.citiesSubscription$ = this.store
      .select('city')
      .pipe(
        map((cityState) => cityState.cities),
        tap((cities) => {
          if (this.map && cities.length === 0) {
            this.markers.forEach((marker) => this.map.removeLayer(marker));
          }
        })
      )
      .subscribe((cities: City[]) => {
        this.cities = cities;

        if (this.map && cities.length !== 0) {
          this.showMarkers();
        }
      });

    this.userSubscription$ = this.store
      .select('auth')
      .pipe(map((authState) => authState.user))
      .subscribe((user: User) => {
        if (user) {
          this.store.dispatch(CityActions.fetchCities());
        }
      });
  }

  ngOnDestroy(): void {
    this.citiesSubscription$?.unsubscribe();
    this.userSubscription$?.unsubscribe();
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
    this.markers = this.markerService.showMarkers(this.cities, this.map);
  }
}
