import { Component, Input, OnInit } from '@angular/core';
import { ThemePalette } from '@angular/material/core';

@Component({
  selector: 'app-event-radius-slider',
  templateUrl: './event-radius-slider.component.html',
  styleUrls: ['./event-radius-slider.component.css'],
})
export class EventRadiusSliderComponent implements OnInit {
  readonly maxRadius = 20;
  @Input()
  value = 0;
  sliderColor: ThemePalette = 'primary';
  // constructor(private store: Store<AppStoreState>) {}

  ngOnInit(): void {
    this.value = 10;
  }

  onRadiusChange(): void {
    console.log(this.value);
  }
}
