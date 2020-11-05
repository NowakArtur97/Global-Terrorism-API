import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';

import MapRoutingModule from './map-routing.module';
import { MapComponent } from './map/map.component';
import { MarkerPopupComponent } from './marker-popup/marker-popup.component';

@NgModule({
  declarations: [MapComponent, MarkerPopupComponent],
  imports: [HttpClientModule, LeafletModule, MapRoutingModule],
  exports: [MapComponent],
})
export default class MapModule {}
