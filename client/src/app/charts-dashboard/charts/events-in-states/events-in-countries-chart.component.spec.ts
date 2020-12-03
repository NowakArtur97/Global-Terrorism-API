import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Store, StoreModule } from '@ngrx/store';
import { ChartsModule } from 'ng2-charts';
import { of } from 'rxjs';
import { MaterialModule } from 'src/app/common/material.module';
import { selectAllEvents } from 'src/app/event/store/event.reducer';
import AppStoreState from 'src/app/store/app.state';

import Event from '../../../event/models/event.model';
import { EventsInCountriesChartComponent } from './events-in-countries-chart.component';

describe('EventsInCountriesChartComponent', () => {
  let component: EventsInCountriesChartComponent;
  let fixture: ComponentFixture<EventsInCountriesChartComponent>;
  let store: Store<AppStoreState>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EventsInCountriesChartComponent],
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
    fixture = TestBed.createComponent(EventsInCountriesChartComponent);
    component = fixture.componentInstance;

    store = TestBed.inject(Store);
  });

  describe('when load chart', () => {
    it('should add up all events in countries', () => {
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
          countryOfOrigin: { id: 7, name: 'country 2' },
        },
        city: {
          id: 16,
          name: 'city 3',
          latitude: 20,
          longitude: 10,
          province: {
            id: 4,
            name: 'province 3',
            country: { id: 7, name: 'country 2' },
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

      console.log(component.barChartData[0]);
      console.log(component.barChartData[0].data[0]);

      expect(component.barChartData[0].data[0]).toEqual(1);
      expect(component.barChartData[0].data[1]).toEqual(2);
    });

    it('and there are no events should chart data be equal to zero', () => {
      spyOn(store, 'select').and.callFake((selector) => {
        if (selector === selectAllEvents) {
          return of([]);
        }
      });

      fixture.detectChanges();
      component.ngOnInit();

      expect(component.barChartData[0].data).toEqual([]);
    });
  });
});
