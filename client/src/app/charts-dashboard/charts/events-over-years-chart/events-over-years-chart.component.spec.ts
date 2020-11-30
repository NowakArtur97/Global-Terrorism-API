import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Store, StoreModule } from '@ngrx/store';
import { ChartsModule } from 'ng2-charts';
import { of } from 'rxjs';
import { MaterialModule } from 'src/app/common/material.module';
import { selectAllEvents } from 'src/app/event/store/event.reducer';
import AppStoreState from 'src/app/store/app.state';

import Event from '../../../event/models/event.model';
import { EventsOverYearsChartComponent } from './events-over-years-chart.component';

describe('EventsOverYearsChartComponent', () => {
  let component: EventsOverYearsChartComponent;
  let fixture: ComponentFixture<EventsOverYearsChartComponent>;
  let store: Store<AppStoreState>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EventsOverYearsChartComponent],
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
    fixture = TestBed.createComponent(EventsOverYearsChartComponent);
    component = fixture.componentInstance;

    store = TestBed.inject(Store);
  });

  describe('when load chart', () => {
    it('should add up all events over the years', () => {
      const event1 = {
        id: 6,
        summary: 'summary',
        motive: 'motive',
        date: new Date(2000, 6, 12),
        isPartOfMultipleIncidents: false,
        isSuccessful: true,
        isSuicidal: false,
        target: {
          id: 3,
          target: 'target',
          countryOfOrigin: { id: 1, name: 'country' },
        },
        city: {
          id: 4,
          name: 'city',
          latitude: 20,
          longitude: 10,
          province: {
            id: 2,
            name: 'province',
            country: { id: 1, name: 'country' },
          },
        },
        victim: {
          id: 5,
          totalNumberOfFatalities: 11,
          numberOfPerpetratorsFatalities: 3,
          totalNumberOfInjured: 14,
          numberOfPerpetratorsInjured: 4,
          valueOfPropertyDamage: 2000,
        },
      };
      const event2 = {
        id: 12,
        summary: 'summary 2',
        motive: 'motive 2',
        date: new Date(1999, 2, 3),
        isPartOfMultipleIncidents: true,
        isSuccessful: false,
        isSuicidal: true,
        target: {
          id: 9,
          target: 'target 2',
          countryOfOrigin: { id: 7, name: 'country 2' },
        },
        city: {
          id: 10,
          name: 'city 2',
          latitude: 10,
          longitude: 20,
          province: {
            id: 8,
            name: 'province 2',
            country: { id: 7, name: 'country 2' },
          },
        },
        victim: {
          id: 11,
          totalNumberOfFatalities: 10,
          numberOfPerpetratorsFatalities: 2,
          totalNumberOfInjured: 11,
          numberOfPerpetratorsInjured: 6,
          valueOfPropertyDamage: 7000,
        },
      };
      const event3 = {
        id: 18,
        summary: 'summary 3',
        motive: 'motive 3',
        date: new Date(1999, 2, 2),
        isPartOfMultipleIncidents: false,
        isSuccessful: true,
        isSuicidal: false,
        target: {
          id: 15,
          target: 'target 3',
          countryOfOrigin: { id: 13, name: 'country 3' },
        },
        city: {
          id: 16,
          name: 'city 3',
          latitude: 20,
          longitude: 10,
          province: {
            id: 4,
            name: 'province 3',
            country: { id: 13, name: 'country 3' },
          },
        },
        victim: {
          id: 17,
          totalNumberOfFatalities: 12,
          numberOfPerpetratorsFatalities: 1,
          totalNumberOfInjured: 1,
          numberOfPerpetratorsInjured: 1,
          valueOfPropertyDamage: 2300,
        },
      };

      const events: Event[] = [event1, event2, event3];
      spyOn(store, 'select').and.callFake((selector) => {
        if (selector === selectAllEvents) {
          return of(events);
        }
      });

      fixture.detectChanges();
      component.ngOnInit();

      expect(component.scatterChartData[0].data[29]).toEqual({ x: 1999, y: 2 });
      expect(component.scatterChartData[0].data[30]).toEqual({ x: 2000, y: 1 });
    });

    it('and there are no events should chart data be equal to zero', () => {
      spyOn(store, 'select').and.callFake((selector) => {
        if (selector === selectAllEvents) {
          return of([]);
        }
      });

      fixture.detectChanges();
      component.ngOnInit();

      expect(component.scatterChartData[0].data[0]).toEqual({
        x: 1970,
        y: 0,
      });
      expect(component.scatterChartData[0].data[29]).toEqual({
        x: 1999,
        y: 0,
      });
      expect(component.scatterChartData[0].data[30]).toEqual({
        x: 2000,
        y: 0,
      });
      expect(
        component.scatterChartData[0].data[
          component.scatterChartData[0].data.length - 1
        ]
      ).toEqual({
        x: new Date().getFullYear(),
        y: 0,
      });
    });
  });
});
