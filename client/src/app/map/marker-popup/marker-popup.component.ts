import { Component, Input, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { Store } from '@ngrx/store';
import { Subscription } from 'rxjs';
import City from 'src/app/city/models/city.model';
import { selectLastDeletedEventId, selectLastUpdatedEvent } from 'src/app/event/store/event.reducer';
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
export class MarkerPopupComponent implements OnInit, OnDestroy {
  private updateSubscription$: Subscription;
  private deleteSubscription$: Subscription;

  @Input()
  event: Event;
  @Input()
  city: City;
  @Input()
  victim: Victim;

  constructor(private store: Store<AppStoreState>) {}

  ngOnInit(): void {
    this.updateSubscription$ = this.store
      .select(selectLastUpdatedEvent)
      .subscribe((lastUpdatedEvent) => {
        if (lastUpdatedEvent?.id === this.event.id) {
          const eventModel = lastUpdatedEvent;
          this.event = eventModel;
          this.city = eventModel.city;
          this.victim = eventModel.victim;
        }
      });

    this.deleteSubscription$ = this.store
      .select(selectLastDeletedEventId)
      .subscribe((lastDeletedEventId) => {
        if (lastDeletedEventId === this.event.id) {
          // TODO: Remove markup
        }
      });
  }

  ngOnDestroy(): void {
    this.updateSubscription$?.unsubscribe();
  }

  updateEvent(): void {
    this.store.dispatch(EventActions.updateEventStart({ id: this.event.id }));
  }

  deleteEvent(): void {
    this.store.dispatch(EventActions.deleteEventStart({ id: this.event.id }));
  }
}
