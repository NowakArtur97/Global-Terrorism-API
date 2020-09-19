import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';

import MapRoutingModule from './map-routing.module';
import { MapComponent } from './map.component';

@NgModule({
  declarations: [MapComponent],
  imports: [HttpClientModule, LeafletModule, MapRoutingModule],
  exports: [MapComponent],
})
export default class MapModule {}
