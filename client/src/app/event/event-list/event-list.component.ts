import { AfterViewInit, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Store } from '@ngrx/store';
import { Subscription } from 'rxjs';
import AppStoreState from 'src/app/store/app.state';

import * as EventActions from '../../event/store/event.actions';
import Event from '../models/event.model';
import { selectAllEvents } from '../store/event.reducer';

@Component({
  selector: 'app-event-list',
  templateUrl: './event-list.component.html',
  styleUrls: ['./event-list.component.css'],
})
export class EventListComponent implements OnInit, OnDestroy, AfterViewInit {
  private eventsRadiusSubscription$: Subscription;

  displayedColumns: string[] = [
    'id',
    'summary',
    'motive',
    'date',
    'update',
    'delete',
  ];
  dataSource: MatTableDataSource<Event>;
  pageSize = 100;

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  constructor(private store: Store<AppStoreState>) {}

  ngOnInit(): void {
    this.eventsRadiusSubscription$ = this.store
      .select(selectAllEvents)
      .subscribe(
        (events: Event[]) => (this.dataSource = new MatTableDataSource(events))
      );
  }

  ngOnDestroy(): void {
    this.eventsRadiusSubscription$?.unsubscribe();
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  applyFilter(event: KeyboardEvent): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  updateEvent(event: Event): void {
    this.store.dispatch(EventActions.startFillingOutForm());
    this.store.dispatch(EventActions.updateEventStart({ id: event.id }));
  }

  deleteEvent(eventToDelete: Event): void {
    this.store.dispatch(EventActions.deleteEventStart({ eventToDelete }));
  }
}
