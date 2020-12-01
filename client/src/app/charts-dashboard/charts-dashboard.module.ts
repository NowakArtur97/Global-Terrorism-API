import { LayoutModule } from '@angular/cdk/layout';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { ChartsModule } from 'ng2-charts';

import { MaterialModule } from '../common/material.module';
import ChartsDashboardRoutingModule from './charts-dashboard-routing.module';
import { EventsOverYearsChartComponent } from './charts/events-over-years-chart/events-over-years-chart.component';
import { VictimsChartComponent } from './charts/victims-chart/victims-chart.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { FatalitiesChartComponent } from './charts/victims-chart/fatalities-chart/fatalities-chart.component';

@NgModule({
  declarations: [
    DashboardComponent,
    VictimsChartComponent,
    EventsOverYearsChartComponent,
    FatalitiesChartComponent,
  ],
  imports: [
    MatGridListModule,
    MatCardModule,
    MatMenuModule,
    MatIconModule,
    MatButtonModule,
    LayoutModule,

    CommonModule,
    ChartsDashboardRoutingModule,
    ChartsModule,
    MaterialModule,
  ],
})
export class ChartsDashboardModule {}
