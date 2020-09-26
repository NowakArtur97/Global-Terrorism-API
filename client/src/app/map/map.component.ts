import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import * as L from 'leaflet';
import { icon } from 'leaflet';
import { Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import City from '../cities/models/city.model';

import AppStoreState from '../store/app.store.state';
import MarkerService from './marker.service';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css'],
})
export class MapComponent implements OnInit, OnDestroy, AfterViewInit {
  cities: City[] = [];
  citiesSubscription: Subscription;
  private map: L.Map;
  private markers: L.Marker[] = [];

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
    this.citiesSubscription = this.store
      .select('cities')
      .pipe(map((citiesState) => citiesState.cities))
      .subscribe((cities: City[]) => {
        this.cities = cities;
        if (this.map && cities && cities.length === 0) {
          this.markers.forEach((marker) => this.map.removeLayer(marker));
        }
      });
  }

  ngOnDestroy(): void {
    this.citiesSubscription.unsubscribe();
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

    this.markers = this.markerService.showMarkers(this.cities, this.map);
  }
}
