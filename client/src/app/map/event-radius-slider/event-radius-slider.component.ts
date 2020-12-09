import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-event-radius-slider',
  templateUrl: './event-radius-slider.component.html',
  styleUrls: ['./event-radius-slider.component.css'],
})
export class EventRadiusSliderComponent implements OnInit {
  readonly maxRadius = 20;
  value = 0;
  // constructor(private store: Store<AppStoreState>) {}

  ngOnInit(): void {}

  onRadiusChange(): void {
    console.log(this.value);
  }
}
