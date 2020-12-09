import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Injector, NgModule } from '@angular/core';
import { createCustomElement } from '@angular/elements';
import { FormsModule } from '@angular/forms';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';

import { MaterialModule } from '../common/material.module';
import { DateSliderComponent } from './date-slider/date-slider.component';
import MapRoutingModule from './map-routing.module';
import { MapComponent } from './map/map.component';
import { MarkerPopupComponent } from './marker-popup/marker-popup.component';
import { EventRadiusSliderComponent } from './event-radius-slider/event-radius-slider.component';

@NgModule({
  declarations: [MapComponent, MarkerPopupComponent, DateSliderComponent, EventRadiusSliderComponent],
  imports: [
    CommonModule,
    HttpClientModule,
    LeafletModule,
    MapRoutingModule,
    FormsModule,
    MaterialModule,
  ],
  exports: [MapComponent],
  entryComponents: [MarkerPopupComponent],
})
export default class MapModule {
  constructor(private injector: Injector) {
    const MarkerPopupElement = createCustomElement(MarkerPopupComponent, {
      injector,
    });
    customElements.define('app-marker-popup-element', MarkerPopupElement);
  }
}
