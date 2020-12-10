import { Component, Input, OnInit } from '@angular/core';
import { ThemePalette } from '@angular/material/core';
import { Store } from '@ngrx/store';
import AppStoreState from 'src/app/store/app.state';

import * as EventActions from '../../event/store/event.actions';

@Component({
  selector: 'app-event-radius-slider',
  templateUrl: './event-radius-slider.component.html',
  styleUrls: ['./event-radius-slider.component.css'],
})
export class EventRadiusSliderComponent implements OnInit {
  readonly maxRadius = 16000000;
  @Input()
  radius = 0;
  sliderColor: ThemePalette = 'primary';
  constructor(private store: Store<AppStoreState>) {}

  ngOnInit(): void {
    this.radius = this.maxRadius / 4;
    this.onRadiusChange();
  }

  onRadiusChange(): void {
    this.store.dispatch(
      EventActions.changeMaxEventsDetectionRadius({ maxRadius: this.radius })
    );
  }
}
