import { Component } from '@angular/core';
import { Label } from 'ng2-charts';

import Event from '../../../../event/models/event.model';
import { AbstractVictimsChartComponent } from '../abstract-victims-chart.component';

@Component({
  selector: 'app-injured-victims-chart',
  templateUrl: './injured-victims-chart.component.html',
  styleUrls: ['./injured-victims-chart.component.css'],
})
export class InjuredVictimsChartComponent extends AbstractVictimsChartComponent {
  pieChartLabels: Label[] = [
    'Number of perpetrators injured',
    'Number of civilians injured',
  ];

  private numberOfPerpetratorsInjured = 0;
  private numberOfCiviliansInjured = 0;

  protected populateChart(events: Event[]): void {
    this.numberOfCiviliansInjured = 0;
    this.numberOfPerpetratorsInjured = 0;

    events.forEach(({ victim }) => {
      this.numberOfPerpetratorsInjured += victim.numberOfPerpetratorsInjured;
      this.numberOfCiviliansInjured +=
        victim.totalNumberOfInjured - victim.numberOfPerpetratorsInjured;
    });

    this.pieChartData[0] = this.numberOfPerpetratorsInjured;
    this.pieChartData[1] = this.numberOfCiviliansInjured;
  }
}
