import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Store, StoreModule } from '@ngrx/store';
import { MaterialModule } from 'src/app/common/material.module';
import AppStoreState from 'src/app/store/app.state';

import * as EventActions from '../../event/store/event.actions';
import { DateSliderComponent } from './date-slider.component';

describe('DateSliderComponent', () => {
  let component: DateSliderComponent;
  let fixture: ComponentFixture<DateSliderComponent>;
  let store: Store<AppStoreState>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DateSliderComponent],
      imports: [
        StoreModule.forRoot({}),
        MaterialModule,
        BrowserAnimationsModule,
      ],
      providers: [Store],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DateSliderComponent);
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
