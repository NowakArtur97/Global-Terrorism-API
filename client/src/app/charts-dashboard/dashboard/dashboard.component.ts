import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Component } from '@angular/core';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent {
  cardLayout = this.breakpointObserver.observe(Breakpoints.Handset).pipe(
    map(({ matches }) => {
      if (matches) {
        return {
          columns: 2,
          eventsOverYearsChart: { cols: 2, rows: 1 },
          eventsInCountriesChart: { cols: 2, rows: 1 },
          fatalVictimsChart: { cols: 1, rows: 1 },
          injuredVictimsChart: { cols: 1, rows: 1 },
        };
      }

      return {
        columns: 2,
        eventsOverYearsChart: { cols: 2, rows: 1 },
        eventsInCountriesChart: { cols: 2, rows: 1 },
        fatalVictimsChart: { cols: 1, rows: 1 },
        injuredVictimsChart: { cols: 1, rows: 1 },
      };
    })
  );

  constructor(private breakpointObserver: BreakpointObserver) {}
}
