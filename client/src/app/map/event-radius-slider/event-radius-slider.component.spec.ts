import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventRadiusSliderComponent } from './event-radius-slider.component';

describe('EventRadiusSliderComponent', () => {
  let component: EventRadiusSliderComponent;
  let fixture: ComponentFixture<EventRadiusSliderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EventRadiusSliderComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EventRadiusSliderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
