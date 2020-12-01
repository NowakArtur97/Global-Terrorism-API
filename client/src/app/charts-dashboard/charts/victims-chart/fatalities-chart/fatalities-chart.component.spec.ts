import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FatalitiesChartComponent } from './fatalities-chart.component';

describe('FatalitiesChartComponent', () => {
  let component: FatalitiesChartComponent;
  let fixture: ComponentFixture<FatalitiesChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FatalitiesChartComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FatalitiesChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
