import { Injectable } from '@angular/core';
import { NgElement, WithProperties } from '@angular/elements';
import * as L from 'leaflet';

import City from '../city/models/city.model';
import Event from '../event/models/event.model';
import { MarkerPopupComponent } from './marker-popup/marker-popup.component';
import * as AuthActions from '../auth/store/auth.actions';

@Injectable({ providedIn: 'root' })
export default class MarkerService {
  private CIRCLE_MARKER_COLOR = '#1B7915';
  private USER_Z_INDEX_OFFSET = -1;
  private maxRadius: number;

  private createMarkerPopup(event: Event): any {
    const { city, victim } = event;
    const markerPopupEl: NgElement &
      WithProperties<MarkerPopupComponent> = document.createElement(
      'app-marker-popup-element'
    ) as any;
    markerPopupEl.event = event;
    markerPopupEl.city = city;
    markerPopupEl.victim = victim;
    return markerPopupEl;
  }

  private scaleRadius = (value: number, maxRadius: number): number =>
    60 * (this.calculateRadius(value, maxRadius) / maxRadius);

  private calculateRadius(value: number, maxRadius: number): number {
    const oneTenthOfMaxVal = maxRadius / 10;
    return value <= oneTenthOfMaxVal ? oneTenthOfMaxVal : value;
  }

  private getMaxRadius(events: Event[]): number {
    return Math.max(
      ...events.map(
        ({ victim }) =>
          victim.totalNumberOfFatalities + victim.totalNumberOfInjured
      ),
      1
    );
  }

  private createCircleMarkerFromEvent(
    event: Event,
    map: L.Map
  ): L.CircleMarker {
    const { city, victim } = event;
    return L.circleMarker([city.latitude, city.longitude], {
      radius: this.scaleRadius(
        victim.totalNumberOfFatalities + victim.totalNumberOfInjured,
        this.maxRadius
      ),
    })
      .addTo(map)
      .bindPopup(this.createMarkerPopup(event));
  }

  createCircleMarker(
    location: L.LatLngExpression,
    radius: number,
    map: L.Map
  ): L.CircleMarker {
    return L.circle(location, {
      radius,
    })
      .setStyle({
        color: this.CIRCLE_MARKER_COLOR,
      })
      .addTo(map);
  }

  removeCircleMarker(marker: L.CircleMarker, map: L.Map): void {
    if (marker) {
      map.removeLayer(marker);
    }
  }

  createCircleMarkersFromEvents(events: Event[], map: L.Map): L.CircleMarker[] {
    const markers: L.CircleMarker[] = [];
    if (events.length === 0) {
      return markers;
    }
    this.maxRadius = this.getMaxRadius(events);
    for (const event of events) {
      markers.push(this.createCircleMarkerFromEvent(event, map));
    }
    return markers;
  }

  removeCircleMarkerByCity(
    map: L.Map,
    markers: L.CircleMarker[],
    city: City
  ): void {
    const markerToDelete = markers.find((marker) => {
      const { lat, lng } = marker.getLatLng();
      const { latitude, longitude } = city;
      return lat === latitude && lng === longitude;
    });
    if (markerToDelete) {
      map.removeLayer(markerToDelete);
    }
  }

  cleanMapFromCircleMarkers(map: L.Map, markers: L.CircleMarker[]): void {
    if (markers.length > 0) {
      markers.forEach((marker) => map.removeLayer(marker));
    }
  }

  createUserPositionMarker(latLong: L.LatLngExpression, map: L.Map): L.Marker {
    const markerText = 'Your position';
    return (
      L.marker(latLong, {
        zIndexOffset: this.USER_Z_INDEX_OFFSET,
        draggable: true,
      })
        // .bindPopup(markerText)
        // .on('mouseover', function (): void {
        //   this.openPopup();
        // })
        .addTo(map)
    );
  }

  removeMarker(marker: L.Marker, map: L.Map): void {
    if (marker) {
      map.removeLayer(marker);
    }
  }
}
