import { Component, OnInit } from '@angular/core';
import { ChartOptions, ChartType } from 'chart.js';
import { Label, SingleDataSet } from 'ng2-charts';

@Component({
  selector: 'app-victims-chart',
  templateUrl: './victims-chart.component.html',
  styleUrls: ['./victims-chart.component.css'],
})
export class VictimsChartComponent implements OnInit {
  public pieChartOptions: ChartOptions = {
    responsive: true,
  };
  public pieChartLabels: Label[] = [
    'Download Sales',
    'In-Store Sales',
    'Mail Sales',
  ];
  public pieChartData: SingleDataSet = [300, 500, 100];
  public pieChartType: ChartType = 'pie';
  public pieChartLegend = true;
  public pieChartPlugins = [];

  constructor() {}

  ngOnInit() {}
}
