import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import AppStoreState from 'src/app/store/app.state';

import * as EventActions from '../../event/store/event.actions';

@Component({
  selector: 'app-event-date-slider',
  templateUrl: './event-date-slider.component.html',
  styleUrls: ['./event-date-slider.component.css'],
})
export class EventDateSliderComponent implements OnInit {
  private readonly minDate = new Date(1970, 0, 1);
  private readonly maxDate = new Date();
  sliderRange: number;
  selectedDate: Date;
  value = 0;

  constructor(private store: Store<AppStoreState>) {}

  private getTimeDifference(): number {
    return Math.abs(this.maxDate.getTime() - this.minDate.getTime());
  }

  ngOnInit(): void {
    this.sliderRange = this.getTimeDifference();
    this.value = this.sliderRange;
    this.selectedDate = new Date(this.maxDate);
  }

  onDateChange(): void {
    const startDate = new Date(this.minDate);
    this.selectedDate = new Date(startDate.getTime() + this.value);

    this.store.dispatch(
      EventActions.changeEndDateOfEvents({ endDateOfEvents: this.selectedDate })
    );
  }
}
