import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ResizeEvent } from 'leaflet';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements OnInit {
  rowHeight: string;

  cardLayout = this.breakpointObserver.observe(Breakpoints.Handset).pipe(
    map(({ matches }) => {
      if (matches) {
        return {
          columns: 2,
          eventsOverYearsChart: { cols: 2, rows: 2 },
          eventsInCountriesChart: { cols: 2, rows: 2 },
          fatalVictimsChart: { cols: 2, rows: 1 },
          injuredVictimsChart: { cols: 2, rows: 1 },
        };
      }

      return {
        columns: 2,
        eventsOverYearsChart: { cols: 2, rows: 2 },
        eventsInCountriesChart: { cols: 2, rows: 2 },
        fatalVictimsChart: { cols: 1, rows: 1 },
        injuredVictimsChart: { cols: 1, rows: 1 },
      };
    })
  );

  constructor(
    private breakpointObserver: BreakpointObserver,
    private titleService: Title
  ) {
    this.titleService.setTitle('Statistics');
  }

  ngOnInit(): void {
    this.rowHeight = window.innerWidth > 640 ? '365px' : '200px';
  }

  onResize(event: ResizeEvent): void {
    this.rowHeight = event.target.innerWidth > 640 ? '365px' : '200px';
  }
}
