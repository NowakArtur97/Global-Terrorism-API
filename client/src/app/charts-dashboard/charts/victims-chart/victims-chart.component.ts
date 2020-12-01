import { Component, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ChartOptions, ChartType } from 'chart.js';
import { SingleDataSet } from 'ng2-charts';
import { Subscription } from 'rxjs';
import { selectAllEvents } from 'src/app/event/store/event.reducer';
import AppStoreState from 'src/app/store/app.state';

import Event from '../../../event/models/event.model';

@Component({
  selector: 'app-victims-chart',
  templateUrl: './victims-chart.component.html',
  styleUrls: ['./victims-chart.component.css'],
})
export abstract class VictimsChartComponent implements OnInit, OnDestroy {
  pieChartOptions: ChartOptions = {
    responsive: true,
  };
  pieChartData: SingleDataSet = [];
  pieChartType: ChartType = 'pie';
  pieChartLegend = true;
  pieChartPlugins = [];

  protected eventsSubscription$: Subscription;

  constructor(private store: Store<AppStoreState>) {}

  ngOnInit(): void {
    this.eventsSubscription$ = this.store
      .select(selectAllEvents)
      .subscribe((events: Event[]) => {
        this.populateChart(events);
      });
  }

  ngOnDestroy(): void {
    this.eventsSubscription$?.unsubscribe();
  }

  protected abstract populateChart(events: Event[]): void;
}
