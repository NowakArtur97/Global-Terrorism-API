import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InjuredVictimsChartComponent } from './injured-victims-chart.component';

describe('InjuredVictimsChartComponent', () => {
  let component: InjuredVictimsChartComponent;
  let fixture: ComponentFixture<InjuredVictimsChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InjuredVictimsChartComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InjuredVictimsChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
