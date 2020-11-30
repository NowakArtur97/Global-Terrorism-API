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
import ChartRoutingModule from './chart-routing.module';
import { DashboardCardComponent } from './dashboard/dashboard-card/dashboard-card.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { VictimsChartComponent } from './victims-chart/victims-chart.component';

@NgModule({
  declarations: [
    DashboardComponent,
    VictimsChartComponent,
    DashboardCardComponent,
  ],
  imports: [
    MatGridListModule,
    MatCardModule,
    MatMenuModule,
    MatIconModule,
    MatButtonModule,
    LayoutModule,

    CommonModule,
    ChartRoutingModule,
    ChartsModule,
    MaterialModule,
  ],
})
export class ChartModule {}
