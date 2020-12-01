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
          fatalitiesChart: { cols: 2, rows: 2 },
          victimChart2: { cols: 2, rows: 3 },
        };
      }

      return {
        columns: 2,
        eventsOverYearsChart: { cols: 2, rows: 1 },
        fatalitiesChart: { cols: 1, rows: 2 },
        victimChart2: { cols: 1, rows: 2 },
      };
    })
  );

  constructor(private breakpointObserver: BreakpointObserver) {}
}
