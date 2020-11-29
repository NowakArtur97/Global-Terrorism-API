import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Store, StoreModule } from '@ngrx/store';
import { MaterialModule } from 'src/app/common/material.module';
import AppStoreState from 'src/app/store/app.state';

import { VictimsChartComponent } from './victims-chart.component';

describe('VictimsChartComponent', () => {
  let component: VictimsChartComponent;
  let fixture: ComponentFixture<VictimsChartComponent>;
  let store: Store<AppStoreState>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [VictimsChartComponent],
      imports: [
        StoreModule.forRoot({}),
        MaterialModule,
        BrowserAnimationsModule,
      ],
      providers: [Store],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VictimsChartComponent);
    component = fixture.componentInstance;

    store = TestBed.inject(Store);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
