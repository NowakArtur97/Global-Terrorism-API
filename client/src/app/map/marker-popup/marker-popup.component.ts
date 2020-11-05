import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';
import City from 'src/app/city/models/city.model';
import Victim from 'src/app/victim/models/victim.model';

import Event from '../../event/models/event.model';

@Component({
  selector: 'app-marker-popup',
  templateUrl: './marker-popup.component.html',
  styleUrls: ['./marker-popup.component.css'],
  encapsulation: ViewEncapsulation.None,
})
export class MarkerPopupComponent implements OnInit {
  @Input()
  event: Event;
  @Input()
  city: City;
  @Input()
  victim: Victim;

  constructor() {}

  ngOnInit(): void {}
}
