import { Component, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ChartDataSets, ChartOptions, ChartType } from 'chart.js';
import { Label } from 'ng2-charts';
import { Subscription } from 'rxjs';
import { selectAllEvents } from 'src/app/event/store/event.reducer';
import AppStoreState from 'src/app/store/app.state';

import Event from '../../../event/models/event.model';

@Component({
  selector: 'app-events-in-countries',
  templateUrl: './events-in-countries.component.html',
  styleUrls: ['./events-in-countries.component.css'],
})
export class EventsInCountriesComponent implements OnInit, OnDestroy {
  barChartOptions: ChartOptions = {
    responsive: true,
  };
  barChartLabels: Label[] = [];
  barChartType: ChartType = 'bar';
  barChartLegend = true;
  barChartPlugins = [];

  barChartData: ChartDataSets[] = [{ data: [], label: 'Events in countries' }];

  private eventsSubscription$: Subscription;

  constructor(private store: Store<AppStoreState>) {}

  ngOnInit(): void {
    this.eventsSubscription$ = this.store
      .select(selectAllEvents)
      .subscribe((events: Event[]) => {});
  }

  ngOnDestroy(): void {
    this.eventsSubscription$?.unsubscribe();
  }
}
