import { Injectable } from '@angular/core';
import { NgElement, WithProperties } from '@angular/elements';
import * as L from 'leaflet';

import Event from '../event/models/event.model';
import { MarkerPopupComponent } from './marker-popup/marker-popup.component';

@Injectable({ providedIn: 'root' })
export default class MarkerService {
  private maxRadius: number;

  private createMarkerPopup(event: Event): any {
    const markerPopupEl: NgElement &
      WithProperties<MarkerPopupComponent> = document.createElement(
      'app-marker-popup-element'
    ) as any;
    markerPopupEl.event = event;
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
        (event) =>
          event.victim.totalNumberOfFatalities +
          event.victim.totalNumberOfInjured
      ),
      0
    );
  }

  private createCircleMarker(event: Event, map: L.Map): L.CircleMarker {
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

  createCircleMarkers(events: Event[] = [], map: L.Map): L.CircleMarker[] {
    this.maxRadius = this.getMaxRadius(events);
    const markers: L.CircleMarker[] = [];
    for (const event of events) {
      markers.push(this.createCircleMarker(event, map));
    }
    return markers;
  }
}
