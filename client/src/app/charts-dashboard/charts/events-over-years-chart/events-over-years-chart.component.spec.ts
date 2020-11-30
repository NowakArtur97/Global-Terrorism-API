import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventsOverYearsChartComponent } from './events-over-years-chart.component';

describe('EventsOverYearsChartComponent', () => {
  let component: EventsOverYearsChartComponent;
  let fixture: ComponentFixture<EventsOverYearsChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EventsOverYearsChartComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EventsOverYearsChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
