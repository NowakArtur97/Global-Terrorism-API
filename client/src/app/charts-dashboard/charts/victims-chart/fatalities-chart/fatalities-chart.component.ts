import { Component } from '@angular/core';
import { Label } from 'ng2-charts';

import Event from '../../../../event/models/event.model';
import { VictimsChartComponent } from '../victims-chart.component';

@Component({
  selector: 'app-fatalities-chart',
  templateUrl: './fatalities-chart.component.html',
  styleUrls: ['./fatalities-chart.component.css'],
})
export class FatalitiesChartComponent extends VictimsChartComponent {
  pieChartLabels: Label[] = [
    'Number of perpetrator fatalities',
    'Number of civilians fatalities',
  ];

  private numberOfPerpetratorsFatalities = 0;
  private numberOfCiviliansFatalities = 0;

  protected populateChart(events: Event[]): void {
    this.numberOfCiviliansFatalities = 0;
    this.numberOfPerpetratorsFatalities = 0;

    events.forEach(({ victim }) => {
      this.numberOfPerpetratorsFatalities +=
        victim.numberOfPerpetratorsFatalities;
      this.numberOfCiviliansFatalities +=
        victim.totalNumberOfFatalities - victim.numberOfPerpetratorsFatalities;
    });

    this.pieChartData[0] = this.numberOfPerpetratorsFatalities;
    this.pieChartData[1] = this.numberOfCiviliansFatalities;
  }
}
