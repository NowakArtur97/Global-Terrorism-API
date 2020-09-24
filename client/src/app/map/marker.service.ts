import { Injectable } from '@angular/core';
import { marker } from 'leaflet';
import * as L from 'leaflet';
import City from 'src/app/cities/models/city.model';

@Injectable({ providedIn: 'root' })
export default class MarkerService {
  showMarkers(cities: City[] = [], map: L.Map): L.Marker[] {
    const markers: L.Marker[] = [];
    for (const city of cities) {
      markers.push(marker([city.latitude, city.longitude]).addTo(map));
    }
    return markers;
  }
}
