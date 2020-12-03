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
          fatalVictimsChart: { cols: 2, rows: 2 },
          injuredVictimsChart: { cols: 2, rows: 3 },
          eventsInCountriesChart: { cols: 2, rows: 4 },
        };
      }

      return {
        columns: 2,
        eventsOverYearsChart: { cols: 1, rows: 1 },
        fatalVictimsChart: { cols: 1, rows: 2 },
        injuredVictimsChart: { cols: 1, rows: 2 },
        eventsInCountriesChart: { cols: 1, rows: 3 },
      };
    })
  );

  constructor(private breakpointObserver: BreakpointObserver) {}
}
