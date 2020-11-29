import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VictimsChartComponent } from './victims-chart.component';

describe('VictimsChartComponent', () => {
  let component: VictimsChartComponent;
  let fixture: ComponentFixture<VictimsChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [VictimsChartComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VictimsChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
