import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { Store } from '@ngrx/store';
import City from 'src/app/city/models/city.model';
import AppStoreState from 'src/app/store/app.state';
import Victim from 'src/app/victim/models/victim.model';

import Event from '../../event/models/event.model';
import * as EventActions from '../../event/store/event.actions';

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

  constructor(private store: Store<AppStoreState>) {}

  ngOnInit(): void {}

  updateEvent() {
    this.store.dispatch(
      EventActions.updateEventStart({ eventToUpdate: this.event })
    );
  }
}
