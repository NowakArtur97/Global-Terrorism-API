import { Component, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ChartOptions, ChartType } from 'chart.js';
import { Label, SingleDataSet } from 'ng2-charts';
import { Subscription } from 'rxjs';
import { selectAllEventsBeforeDate } from 'src/app/event/store/event.reducer';
import AppStoreState from 'src/app/store/app.state';

import Event from '../../event/models/event.model';

@Component({
  selector: 'app-victims-chart',
  templateUrl: './victims-chart.component.html',
  styleUrls: ['./victims-chart.component.css'],
})
export class VictimsChartComponent implements OnInit, OnDestroy {
  public pieChartOptions: ChartOptions = {
    responsive: true,
  };
  public pieChartLabels: Label[] = [
    'Total number of fatalities',
    'Number of perpetrator fatalities',
    'Total number of injured',
    'Number of perpetrators injured',
  ];

  public pieChartData: SingleDataSet = [];
  public pieChartType: ChartType = 'pie';
  public pieChartLegend = true;
  public pieChartPlugins = [];

  private totalNumberOfFatalities = 0;
  private numberOfPerpetratorFatalities = 0;
  private totalNumberOfInjured = 0;
  private numberOfPerpetratorInjured = 0;

  private eventsSubscription$: Subscription;
  events: Event[] = [];

  constructor(private store: Store<AppStoreState>) {}

  ngOnInit(): void {
    this.eventsSubscription$ = this.store
      .select(selectAllEventsBeforeDate)
      .subscribe((events: Event[]) => {
        this.events = events;
        this.events.forEach(({ victim }) => {
          this.totalNumberOfFatalities += victim.totalNumberOfFatalities;
          this.numberOfPerpetratorFatalities +=
            victim.numberOfPerpetratorFatalities;
          this.totalNumberOfInjured += victim.totalNumberOfInjured;
          this.numberOfPerpetratorInjured += victim.numberOfPerpetratorInjured;
        });

        this.pieChartData[0] = this.totalNumberOfFatalities;
        this.pieChartData[1] = this.numberOfPerpetratorFatalities;
        this.pieChartData[2] = this.totalNumberOfInjured;
        this.pieChartData[3] = this.numberOfPerpetratorInjured;
      });
  }

  ngOnDestroy(): void {
    this.eventsSubscription$?.unsubscribe();
  }
}
