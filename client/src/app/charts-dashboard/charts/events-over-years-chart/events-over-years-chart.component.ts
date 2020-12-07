import { Component, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ChartDataSets, ChartOptions, ChartPoint, ChartType } from 'chart.js';
import { Subscription } from 'rxjs';
import { selectAllEvents } from 'src/app/event/store/event.reducer';
import AppStoreState from 'src/app/store/app.state';

import Event from '../../../event/models/event.model';

@Component({
  selector: 'app-events-over-years-chart',
  templateUrl: './events-over-years-chart.component.html',
  styleUrls: ['./events-over-years-chart.component.css'],
})
export class EventsOverYearsChartComponent implements OnInit, OnDestroy {
  scatterChartOptions: ChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
  };

  scatterChartData: ChartDataSets[] = [
    {
      data: [],
      label: 'Events over the years',
      pointRadius: 10,
    },
  ];
  scatterChartType: ChartType = 'scatter';

  private eventsSubscription$: Subscription;

  constructor(private store: Store<AppStoreState>) {}

  ngOnInit(): void {
    this.eventsSubscription$ = this.store
      .select(selectAllEvents)
      .subscribe((events: Event[]) => this.populateChart(events));
  }

  ngOnDestroy(): void {
    this.eventsSubscription$?.unsubscribe();
  }

  private populateChart(events: Event[]): void {
    this.cleanChartData();

    events.forEach(({ date }) => {
      const year = new Date(date).getFullYear();
      const eventsOverYear: number | number[] | ChartPoint = [
        ...this.scatterChartData[0].data,
      ].find((data: ChartPoint) => data.x === year);
      if (
        typeof eventsOverYear !== 'number' &&
        !Array.isArray(eventsOverYear)
      ) {
        eventsOverYear.y = +eventsOverYear.y + 1;
      }
    });
  }

  private cleanChartData(): void {
    const actualYear = new Date().getFullYear();
    let index = 0;
    for (let year = 1970; year <= actualYear; year++) {
      this.scatterChartData[0].data[index] = { x: year, y: 0 };
      index++;
    }
  }
}
