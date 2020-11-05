import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';

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

  constructor() {}

  ngOnInit(): void {}
}
