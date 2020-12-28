import { animate, state, style, transition, trigger } from '@angular/animations';
import { SelectionModel } from '@angular/cdk/collections';
import { AfterViewInit, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Store } from '@ngrx/store';
import { Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import User from 'src/app/auth/models/user.model';
import AppStoreState from 'src/app/store/app.state';

import * as EventActions from '../../event/store/event.actions';
import Event from '../models/event.model';
import EventService from '../services/event.service';
import { selectAllEvents } from '../store/event.reducer';

@Component({
  selector: 'app-event-list',
  templateUrl: './event-list.component.html',
  styleUrls: ['./event-list.component.css'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({ height: '0', minHeight: '0' })),
      state('expanded', style({ height: '*' })),
      transition(
        'expanded <=> collapsed',
        animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')
      ),
    ]),
  ],
})
export class EventListComponent implements OnInit, OnDestroy, AfterViewInit {
  private userSubscription$: Subscription;
  private eventsSubscription$: Subscription;
  private user: User;

  displayedColumns: string[] = [
    'select',
    'id',
    'target.target',
    'city.name',
    'date',
    'update',
    'delete',
  ];
  dataSource: MatTableDataSource<Event>;
  selection = new SelectionModel<Event>(true, []);
  pageSize = 100;
  expandedElement: Event;

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  constructor(
    private store: Store<AppStoreState>,
    private eventService: EventService
  ) {}

  ngOnInit(): void {
    this.userSubscription$ = this.store
      .select('auth')
      .pipe(map((authStore) => authStore.user))
      .subscribe((user: User) => (this.user = user));

    this.eventsSubscription$ = this.store
      .select(selectAllEvents)
      .subscribe(
        (events: Event[]) => (this.dataSource = new MatTableDataSource(events))
      );
  }

  ngOnDestroy(): void {
    this.eventsSubscription$?.unsubscribe();
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.dataSource.sortingDataAccessor = (data, sortHeaderId: string) =>
      this.getPropertyByPath(data, sortHeaderId);
  }

  applyFilter(event: KeyboardEvent): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  masterToggle(): void {
    this.isAllSelected()
      ? this.selection.clear()
      : this.dataSource.data.forEach((row) => this.selection.select(row));
  }

  isAllSelected(): boolean {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;

    return numSelected === numRows;
  }

  checkboxLabel(row?: Event): string {
    if (!row) {
      return `${this.isAllSelected() ? 'select' : 'deselect'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${
      row.id + 1
    }`;
  }

  updateEvent(event: Event): void {
    this.store.dispatch(EventActions.startFillingOutForm());
    this.store.dispatch(EventActions.updateEventStart({ id: event.id }));
  }

  deleteEvent(eventToDelete: Event): void {
    this.deleteEventFromDataSource(eventToDelete);
    this.store.dispatch(EventActions.deleteEventStart({ eventToDelete }));
  }

  // TODO: Check select all strange behavior
  deleteSelectedEvents(): void {
    const selectedEvents = this.selection.selected;
    this.eventService.deleteAll(selectedEvents, this.user);
  }

  private deleteEventFromDataSource(eventToDelete: Event): void {
    const eventIndex = this.dataSource.data.indexOf(eventToDelete);
    this.dataSource.data.splice(eventIndex, 1);
    this.dataSource._updateChangeSubscription();
  }

  private getPropertyByPath(event: Event, pathString: string): string {
    return pathString.split('.').reduce((o, i) => o[i], event);
  }
}
