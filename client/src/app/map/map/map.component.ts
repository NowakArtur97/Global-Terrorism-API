import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import * as L from 'leaflet';
import { icon } from 'leaflet';
import { Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import Event from '../../event/models/event.model';

import MarkerService from '../services/marker.service';
import * as EventActions from '../../event/store/event.actions';
import * as AuthActions from '../../auth/store/auth.actions';
import {
  selectAllEventsInRadius,
  selectLastDeletedEvent,
  selectMaxRadiusOfEventsDetection,
} from 'src/app/event/store/event.reducer';
import AppStoreState from 'src/app/store/app.state';
import { selectAuthState } from 'src/app/auth/store/auth.reducer';
import ShapeService from '../services/shape.service';

const ICO_SIZE: L.PointExpression = [25, 41];
const ICON_ANCHOR: L.PointExpression = [13, 41];
const ICON_URL = 'assets/leaflet/marker-icon.png';
const SHADOW_ICON_URL = 'assets/leaflet/marker-shadow.png';
const ICON_RETINA_URL = 'assets/leaflet/marker-icon-2x.png';
const iconDefault = icon({
  iconSize: ICO_SIZE,
  iconAnchor: ICON_ANCHOR,
  iconUrl: ICON_URL,
  iconRetinaUrl: ICON_RETINA_URL,
  shadowUrl: SHADOW_ICON_URL,
});
L.Marker.prototype.options.icon = iconDefault;

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css'],
})
export class MapComponent implements OnInit, OnDestroy, AfterViewInit {
  private readonly ZOOM = 4;
  private readonly MAX_ZOOM = 19;
  private readonly TILE_LAYER =
    'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
  private readonly TILES_ATRIBUTION =
    '© <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>';
  private readonly DEFAULT_COUNTRY_STYLE: L.PathOptions = {
    weight: 2,
    opacity: 0.5,
    color: '#1ab2ff',
    fillOpacity: 0.8,
    fillColor: '#66ccff',
  };
  private readonly HIGHLIGHTED_COUNTRY_STYLE: L.PathOptions = {
    weight: 7,
    opacity: 1.0,
    color: '#00334d',
    fillOpacity: 1.0,
    fillColor: '#006699',
  };

  private map: L.Map;
  private markers: L.CircleMarker[] = [];
  private countries: any;

  private eventsSubscription$: Subscription;
  private eventsRadiusSubscription$: Subscription;
  private userSubscription$: Subscription;
  private deleteEventSubscription$: Subscription;
  private countriesShapeSubscription$: Subscription;

  private userLocation: L.LatLngExpression = [50, 18];
  private maxRadiusOfEventsDetection: number;
  private eventsRadiusMarker: L.CircleMarker<any>;
  private userPositionMarker: L.Marker<any>;
  private events: Event[] = [];

  isUserLoggedIn = false;
  highlightedCountry: any;

  constructor(
    private store: Store<AppStoreState>,
    private markerService: MarkerService,
    private shapeService: ShapeService
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
    this.countriesShapeSubscription$?.unsubscribe();
  }

  ngAfterViewInit(): void {
    this.initMap();
    // TODO: better approach would be to pre-load the data in a resolver
    this.countriesShapeSubscription$ = this.shapeService
      .getCountriesShapes()
      .subscribe((countries) => {
        this.countries = countries;
        this.initCountriesLayer();
      });
  }

  private setupSubscriptions(): void {
    this.userSubscription$ = this.store
      .select(selectAuthState)
      .pipe(map((authState) => authState.user))
      .subscribe((user) => {
        this.cleanUserMarkers();
        if (user) {
          this.isUserLoggedIn = true;
          this.store.dispatch(EventActions.fetchEvents());
          this.setupUserMarkers();
        } else {
          this.isUserLoggedIn = false;
        }
      });

    this.eventsSubscription$ = this.store
      .select(selectAllEventsInRadius)
      .subscribe((events) => {
        this.events = events;
        if (this.map && this.events?.length > 0) {
          this.markerService.cleanMapFromCircleMarkers(this.map, this.markers);
          this.markers = this.markerService.createCircleMarkersFromEvents(
            this.events,
            this.map
          );
        }
      });

    this.eventsRadiusSubscription$ = this.store
      .select(selectMaxRadiusOfEventsDetection)
      .subscribe((maxRadiusOfEventsDetection) => {
        this.maxRadiusOfEventsDetection = maxRadiusOfEventsDetection;
        if (this.map && this.maxRadiusOfEventsDetection > 0) {
          this.markerService.removeCircleMarker(
            this.eventsRadiusMarker,
            this.map
          );
          this.initEventRadiusMarker();
        }
      });

    this.deleteEventSubscription$ = this.store
      .select(selectLastDeletedEvent)
      .subscribe((lastDeletedEvent) => {
        if (lastDeletedEvent) {
          this.markerService.removeCircleMarkerByCity(
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

  private initCountriesLayer(): void {
    const countriesLayer = L.geoJSON(this.countries, {
      style: () => this.DEFAULT_COUNTRY_STYLE,
      onEachFeature: (feature, layer) =>
        layer.on({
          mouseover: (e) => {
            this.highlightCountry(e, feature);
          },
          mouseout: (e) => this.resetCountry(e),
        }),
    }).bringToFront();

    this.map.addLayer(countriesLayer);
  }

  private highlightCountry(event: L.LeafletMouseEvent, feature: any): void {
    const layer = event.target;
    this.highlightedCountry = feature.properties.name;
    layer.setStyle(this.HIGHLIGHTED_COUNTRY_STYLE);
  }

  private resetCountry(event: L.LeafletMouseEvent): void {
    const layer = event.target;
    layer.setStyle(this.DEFAULT_COUNTRY_STYLE);
  }

  private initEventRadiusMarker(): void {
    this.eventsRadiusMarker = this.markerService.createCircleMarker(
      this.userLocation,
      this.maxRadiusOfEventsDetection,
      this.map
    );
    this.map?.setView(this.userLocation, this.ZOOM);
  }

  private getUserLocation(): void {
    if (!navigator.geolocation) {
      // TODO: popup with info
      console.log('Location is not supported');
      return;
    }
    navigator.geolocation.getCurrentPosition((position) => {
      const coords = position.coords;
      this.userLocation = [coords.latitude, coords.longitude];
      this.store.dispatch(
        AuthActions.setUserLocation({ userLocation: this.userLocation })
      );

      if (this.isUserLoggedIn && this.userPositionMarker) {
        this.userPositionMarker.on('dragend', () =>
          this.onUserPositionChange()
        );
      }
    });
  }

  private setupUserMarkers(): void {
    if (this.map && this.userLocation) {
      this.userPositionMarker = this.markerService.createUserPositionMarker(
        this.userLocation,
        this.map
      );
      this.userPositionMarker.on('dragend', () => this.onUserPositionChange());
      this.initEventRadiusMarker();
    }
  }

  private cleanUserMarkers(): void {
    this.markerService.removeCircleMarker(this.eventsRadiusMarker, this.map);
    this.eventsRadiusMarker = null;
    this.markerService.removeMarker(this.userPositionMarker, this.map);
    this.userPositionMarker = null;
  }

  private onUserPositionChange(): void {
    const newLatitude = this.userPositionMarker.getLatLng().lat;
    const newLongitude = this.userPositionMarker.getLatLng().lng;
    const userLocation: L.LatLngExpression = [newLatitude, newLongitude];
    this.store.dispatch(AuthActions.setUserLocation({ userLocation }));
    this.userLocation = userLocation;
    this.cleanUserMarkers();
    this.setupUserMarkers();
  }
}
