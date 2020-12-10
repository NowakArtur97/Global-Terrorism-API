import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventRadiusSliderComponent } from './event-radius-slider.component';
import * as EventActions from '../../event/store/event.actions';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Store, StoreModule } from '@ngrx/store';
import { MaterialModule } from 'src/app/common/material.module';
import AppStoreState from 'src/app/store/app.state';

describe('EventRadiusSliderComponent', () => {
  let component: EventRadiusSliderComponent;
  let fixture: ComponentFixture<EventRadiusSliderComponent>;
  let store: Store<AppStoreState>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EventRadiusSliderComponent],
      imports: [
        StoreModule.forRoot({}),
        MaterialModule,
        BrowserAnimationsModule,
      ],
      providers: [Store],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EventRadiusSliderComponent);
    component = fixture.componentInstance;

    store = TestBed.inject(Store);

    spyOn(store, 'dispatch');

    fixture.detectChanges();
    component.ngOnInit();
  });

  describe('when radius change on slider', () => {
    it('should dispatch changeMaxRadiusOfEventsDetection action', () => {
      component.onRadiusChange();

      expect(store.dispatch).toHaveBeenCalledWith(
        EventActions.changeMaxRadiusOfEventsDetection({
          maxRadiusOfEventsDetection: component.radius,
        })
      );
    });
  });
});
