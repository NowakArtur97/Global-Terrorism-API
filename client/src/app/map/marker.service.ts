import { Injectable } from '@angular/core';
import * as L from 'leaflet';
import * as R from 'leaflet-responsive-popup';

import Event from '../event/models/event.model';

@Injectable({ providedIn: 'root' })
export default class MarkerService {
  private maxRadius: number;

  private createMarkerPopup(event: Event): any {
    const { victim } = event;
    return R.responsivePopup({
      hasTip: true,
      offset: [10, 10],
    }).setContent(`
    <div>
    <p>Summary: ${event.summary}</p>
    <p>Motive: ${event.motive}</p>
    <p>Date: ${event.date}</p>
    <p>Total number of fatalities: ${victim.totalNumberOfFatalities}</p>
    <p>Total number of injured: ${victim.totalNumberOfInjured}</p>
    </div>
    `);
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
