import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import AppStoreState from 'src/app/store/app.state';

import * as EventActions from '../../event/store/event.actions';

@Component({
  selector: 'app-date-slider',
  templateUrl: './date-slider.component.html',
  styleUrls: ['./date-slider.component.css'],
})
export class DateSliderComponent implements OnInit {
  private readonly minDate = new Date(1970, 0, 1);
  private readonly maxDate = new Date();
  sliderRange: number;
  selectedDate: Date;
  value = 0;
  constructor(private store: Store<AppStoreState>) {}

  ngOnInit(): void {
    this.sliderRange = this.getTimeDifference();
    this.value = this.sliderRange;
    this.selectedDate = new Date(this.maxDate);
  }

  private getTimeDifference(): number {
    return Math.abs(this.maxDate.getTime() - this.minDate.getTime());
  }

  onDateChange(): void {
    const startDate = new Date(this.minDate);
    this.selectedDate = new Date(startDate.getTime() + this.value);

    this.store.dispatch(
      EventActions.changeMaxEventsDate({ maxDate: this.selectedDate })
    );
  }
}
