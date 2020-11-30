import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Store, StoreModule } from '@ngrx/store';
import { ChartsModule } from 'ng2-charts';
import { of } from 'rxjs';
import { MaterialModule } from 'src/app/common/material.module';
import { selectAllEvents } from 'src/app/event/store/event.reducer';
import AppStoreState from 'src/app/store/app.state';

import Event from '../../event/models/event.model';
import { VictimsChartComponent } from './victims-chart.component';

describe('VictimsChartComponent', () => {
  let component: VictimsChartComponent;
  let fixture: ComponentFixture<VictimsChartComponent>;
  let store: Store<AppStoreState>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [VictimsChartComponent],
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
    fixture = TestBed.createComponent(VictimsChartComponent);
    component = fixture.componentInstance;

    store = TestBed.inject(Store);
  });

  describe('when load chart', () => {
    it('should add up all victims data', () => {
      const event1 = {
        id: 6,
        summary: 'summary',
        motive: 'motive',
        date: new Date(1999, 6, 12),
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
          numberOfPerpetratorFatalities: 3,
          totalNumberOfInjured: 14,
          numberOfPerpetratorInjured: 4,
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
          numberOfPerpetratorFatalities: 2,
          totalNumberOfInjured: 11,
          numberOfPerpetratorInjured: 6,
          valueOfPropertyDamage: 7000,
        },
      };

      const events: Event[] = [event1, event2];
      spyOn(store, 'select').and.callFake((selector) => {
        if (selector === selectAllEvents) {
          return of(events);
        }
      });

      fixture.detectChanges();
      component.ngOnInit();

      expect(component.pieChartData[0]).toEqual(
        event1.victim.totalNumberOfFatalities +
          event2.victim.totalNumberOfFatalities
      );
      expect(component.pieChartData[1]).toEqual(
        event1.victim.numberOfPerpetratorFatalities +
          event2.victim.numberOfPerpetratorFatalities
      );
      expect(component.pieChartData[2]).toEqual(
        event1.victim.totalNumberOfInjured + event2.victim.totalNumberOfInjured
      );
      expect(component.pieChartData[3]).toEqual(
        event1.victim.numberOfPerpetratorInjured +
          event2.victim.numberOfPerpetratorInjured
      );
    });

    it('and there are no events should chart data be equal to zero', () => {
      spyOn(store, 'select').and.callFake((selector) => {
        if (selector === selectAllEvents) {
          return of([]);
        }
      });

      fixture.detectChanges();
      component.ngOnInit();

      expect(component.pieChartData[0]).toEqual(0);
      expect(component.pieChartData[1]).toEqual(0);
      expect(component.pieChartData[2]).toEqual(0);
      expect(component.pieChartData[3]).toEqual(0);
    });
  });
});
