import { Injectable } from '@angular/core';
import { marker } from 'leaflet';
import * as L from 'leaflet';

import Event from '../event/models/event.model';

@Injectable({ providedIn: 'root' })
export default class MarkerService {
  private createMarkerPopup(event: Event): string {
    return `<div>
    <p>Summary: ${event.summary}</p>
    <p>Motive: ${event.motive}</p>
    <p>Date: ${event.date}</p>
    </div>
    `;
  }

  showMarkers(events: Event[] = [], map: L.Map): L.Marker[] {
    const markers: L.Marker[] = [];
    for (const event of events) {
      const { city } = event;
      markers.push(
        marker([city.latitude, city.longitude])
          .addTo(map)
          .bindPopup(this.createMarkerPopup(event))
      );
    }
    return markers;
  }
}
