import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import { selectAllEventsBeforeDate } from 'src/app/event/store/event.reducer';
import AppStoreState from 'src/app/store/app.state';
import Event from '../../event/models/event.model';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements OnInit, OnDestroy {
  /** Based on the screen size, switch from standard to one column per row */
  cards = this.breakpointObserver.observe(Breakpoints.Handset).pipe(
    map(({ matches }) => {
      if (matches) {
        return [{ title: 'Card 1', cols: 2, rows: 2 }];
      }

      return [{ title: 'Card 1', cols: 2, rows: 2 }];
    })
  );
  private eventsSubscription$: Subscription;
  events: Event[] = [];

  constructor(
    private breakpointObserver: BreakpointObserver,
    private store: Store<AppStoreState>
  ) {}

  ngOnInit(): void {
    this.eventsSubscription$ = this.store
      .select(selectAllEventsBeforeDate)
      .subscribe((events: Event[]) => {
        this.events = events;
        console.log(events);
      });
  }

  ngOnDestroy(): void {
    this.eventsSubscription$?.unsubscribe();
  }
}
