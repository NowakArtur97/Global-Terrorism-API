import { Injectable } from '@angular/core';
import { marker } from 'leaflet';
import * as L from 'leaflet';

import City from '../models/city.model';

@Injectable({ providedIn: 'root' })
export default class MarkerService {
  showMarkers(cities: City[] = [], map: L.Map) {
    for (const city of cities) {
      marker([city.latitude, city.longitude]).addTo(map);
    }
  }
}
