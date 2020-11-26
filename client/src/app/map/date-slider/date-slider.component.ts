import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-date-slider',
  templateUrl: './date-slider.component.html',
  styleUrls: ['./date-slider.component.css'],
})
export class DateSliderComponent implements OnInit {
  private minDate = new Date(1970, 0, 1);
  private maxDate = new Date();
  sliderRange: number;
  selectedDate: Date;
  value = 0;
  constructor() {}

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

    // TODO: Filter  events
    console.log(this.selectedDate);
  }
}
