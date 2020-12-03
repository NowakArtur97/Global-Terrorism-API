import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Store, StoreModule } from '@ngrx/store';
import { ChartsModule } from 'ng2-charts';
import { of } from 'rxjs';
import { MaterialModule } from 'src/app/common/material.module';
import { selectAllEvents } from 'src/app/event/store/event.reducer';
import AppStoreState from 'src/app/store/app.state';

import { EventsInCountriesComponent } from './events-in-countries.component';

describe('EventsInCountriesComponent', () => {
  let component: EventsInCountriesComponent;
  let fixture: ComponentFixture<EventsInCountriesComponent>;
  let store: Store<AppStoreState>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EventsInCountriesComponent],
      imports: [
        StoreModule.forRoot({}),
        MaterialModule,
        BrowserAnimationsModule,
        ChartsModule,
      ],
      providers: [Store],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EventsInCountriesComponent);
    component = fixture.componentInstance;

    store = TestBed.inject(Store);

    spyOn(store, 'select').and.callFake((selector) => {
      if (selector === selectAllEvents) {
        return of([]);
      }
    });

    fixture.detectChanges();
    component.ngOnInit();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
