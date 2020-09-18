import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { icon } from 'leaflet';
import * as L from 'leaflet';
import { Subscription } from 'rxjs';
import { map } from 'rxjs/operators';

import AppStoreState from '../../store/app.store.state';
import City from '../models/city.model';
import MarkerService from '../services/marker.service';
import * as CitiesActions from '../store/cities.actions';

@Component({
  selector: 'app-cities-map',
  templateUrl: './cities-map.component.html',
  styleUrls: ['./cities-map.component.css'],
})
export class CitiesMapComponent implements OnInit, OnDestroy, AfterViewInit {
  cities: City[] = [];
  citiesSubscription: Subscription;
  private map: L.Map;

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
        this.markerService.showMarkers(this.cities, this.map);
      });
  }

  ngOnDestroy() {
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
  }

  onFetchCities() {
    this.store.dispatch(CitiesActions.fetchCitites());
  }
}
