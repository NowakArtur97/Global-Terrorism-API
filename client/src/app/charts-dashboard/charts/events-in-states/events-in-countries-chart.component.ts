import { Component, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ChartDataSets, ChartOptions, ChartType } from 'chart.js';
import { Label } from 'ng2-charts';
import { Subscription } from 'rxjs';
import { selectAllEvents } from 'src/app/event/store/event.reducer';
import AppStoreState from 'src/app/store/app.state';

import Event from '../../../event/models/event.model';

@Component({
  selector: 'app-events-in-countries-chart',
  templateUrl: './events-in-countries-chart.component.html',
  styleUrls: ['./events-in-countries-chart.component.css'],
})
export class EventsInCountriesChartComponent implements OnInit, OnDestroy {
  barChartOptions: ChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
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
      .subscribe((events: Event[]) => this.populateChart(events));
  }

  ngOnDestroy(): void {
    this.eventsSubscription$?.unsubscribe();
  }

  private populateChart(events: Event[]): void {
    this.barChartLabels = [];
    events.forEach((event) => {
      const country = event.target.countryOfOrigin.name;
      const countryIndex = this.barChartLabels.indexOf(country);
      if (countryIndex === -1) {
        const newIndex = this.barChartLabels.push(country) - 1;
        this.barChartData[0].data[newIndex] = 1;
      } else {
        const eventsInCountry = this.barChartData[0].data[countryIndex];
        if (typeof eventsInCountry === 'number') {
          this.barChartData[0].data[countryIndex] = eventsInCountry + 1;
        }
      }
    });
  }

  onResize(): void {
    this.barChartData = this.barChartData.slice();
  }
}
