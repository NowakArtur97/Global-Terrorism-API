import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Store, StoreModule } from '@ngrx/store';
import { of } from 'rxjs';
import { MaterialModule } from 'src/app/common/material.module';
import { selectLastUpdatedEvent } from 'src/app/event/store/event.reducer';
import AppStoreState from 'src/app/store/app.state';

import * as EventActions from '../../event/store/event.actions';
import { MarkerPopupComponent } from './marker-popup.component';

describe('MarkerPopupComponent', () => {
  let component: MarkerPopupComponent;
  let fixture: ComponentFixture<MarkerPopupComponent>;
  let store: Store<AppStoreState>;
  const event = {
    id: 6,
    summary: 'summary',
    motive: 'motive',
    date: new Date(),
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

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MarkerPopupComponent],
      imports: [
        StoreModule.forRoot({}),
        MaterialModule,
        BrowserAnimationsModule,
      ],
      providers: [Store],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MarkerPopupComponent);
    component = fixture.componentInstance;

    store = TestBed.inject(Store);
    spyOn(store, 'select').and.callFake((selector) => {
      if (selector === selectLastUpdatedEvent) {
        return of(event);
      }
    });
    spyOn(store, 'dispatch');

    component.event = event;
    component.city = event.city;
    component.victim = event.victim;

    fixture.detectChanges();
    component.ngOnInit();
  });

  describe('when update event', () => {
    it('should dispatch updateEventStart action', () => {
      component.updateEvent();

      expect(store.dispatch).toHaveBeenCalledWith(
        EventActions.updateEventStart({ id: event.id })
      );
    });
  });

  describe('when delete event', () => {
    it('should dispatch deleteEventStart action', () => {
      component.deleteEvent();

      expect(store.dispatch).toHaveBeenCalledWith(
        EventActions.deleteEventStart({ eventToDelete: event })
      );
    });
  });
});
