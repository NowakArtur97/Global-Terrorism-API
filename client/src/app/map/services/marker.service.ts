import { Injectable } from '@angular/core';
import { NgElement, WithProperties } from '@angular/elements';
import * as L from 'leaflet';

import City from '../../city/models/city.model';
import Event from '../../event/models/event.model';
import { MarkerPopupComponent } from '../marker-popup/marker-popup.component';

@Injectable({ providedIn: 'root' })
export default class MarkerService {
  private readonly MARKER_POPUP_TAG = 'app-marker-popup-element';
  private maxRadius: number;

  private createMarkerPopup(event: Event): any {
    const { city, victim } = event;
    const markerPopupEl: NgElement &
      WithProperties<MarkerPopupComponent> = document.createElement(
      this.MARKER_POPUP_TAG
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
    map: L.Map,
    styles: L.PathOptions
  ): L.CircleMarker {
    const { city, victim } = event;
    return L.circleMarker([city.latitude, city.longitude], {
      radius: this.scaleRadius(
        victim.totalNumberOfFatalities + victim.totalNumberOfInjured,
        this.maxRadius
      ),
    })
      .setStyle(styles)
      .addTo(map)
      .bindPopup(this.createMarkerPopup(event))
      .bringToFront();
  }

  createCircleMarker(
    location: L.LatLngExpression,
    radius: number,
    map: L.Map,
    styles: L.PathOptions
  ): L.Circle {
    return L.circle(location, {
      radius,
    })
      .setStyle(styles)
      .addTo(map)
      .bringToBack();
  }

  removeCircleMarker(marker: L.CircleMarker, map: L.Map): void {
    if (marker) {
      map.removeLayer(marker);
    }
  }

  createCircleMarkersFromEvents(
    events: Event[],
    map: L.Map,
    styles: L.PathOptions
  ): L.CircleMarker[] {
    const markers: L.CircleMarker[] = [];
    if (events.length === 0) {
      return markers;
    }
    this.maxRadius = this.getMaxRadius(events);
    for (const event of events) {
      markers.push(this.createCircleMarkerFromEvent(event, map, styles));
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
    const markerText = 'Drag and drop me to change location';
    const popupTextOffset: L.PointExpression = [0, -30];
    const popup = L.popup({
      offset: popupTextOffset,
    }).setContent(markerText);

    return L.marker(latLong, {
      draggable: true,
    })
      .bindPopup(popup)
      .on('mouseover', function (): void {
        this.openPopup();
      })
      .addTo(map);
  }

  removeMarker(marker: L.Marker, map: L.Map): void {
    if (marker) {
      map.removeLayer(marker);
    }
  }
}
