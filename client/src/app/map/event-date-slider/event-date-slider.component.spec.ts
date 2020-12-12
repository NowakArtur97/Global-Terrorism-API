import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Store, StoreModule } from '@ngrx/store';
import { MaterialModule } from 'src/app/common/material.module';
import AppStoreState from 'src/app/store/app.state';

import * as EventActions from '../../event/store/event.actions';
import { EventDateSliderComponent } from './event-date-slider.component';

describe('EventDateSliderComponent', () => {
  let component: EventDateSliderComponent;
  let fixture: ComponentFixture<EventDateSliderComponent>;
  let store: Store<AppStoreState>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EventDateSliderComponent],
      imports: [
        StoreModule.forRoot({}),
        MaterialModule,
        BrowserAnimationsModule,
      ],
      providers: [Store],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EventDateSliderComponent);
    component = fixture.componentInstance;

    store = TestBed.inject(Store);

    spyOn(store, 'dispatch');

    fixture.detectChanges();
    component.ngOnInit();
  });

  describe('when date change on slider', () => {
    it('should dispatch changeEndDateOfEvents action', () => {
      component.onDateChange();

      expect(store.dispatch).toHaveBeenCalledWith(
        EventActions.changeEndDateOfEvents({
          endDateOfEvents: component.selectedDate,
        })
      );
    });
  });
});
