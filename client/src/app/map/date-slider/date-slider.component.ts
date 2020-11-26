import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-date-slider',
  templateUrl: './date-slider.component.html',
  styleUrls: ['./date-slider.component.css'],
})
export class DateSliderComponent implements OnInit {
  min = new Date(1970, 0, 1);
  max = new Date();
  date = new Date();
  value = 0;
  constructor() {}

  ngOnInit(): void {}

  formatLabel(value: Date): string {
    return value.toTimeString();
  }
}
