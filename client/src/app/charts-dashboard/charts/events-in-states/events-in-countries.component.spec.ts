import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventsInCountriesComponent } from './events-in-countries.component';

describe('EventsInCountriesComponent', () => {
  let component: EventsInCountriesComponent;
  let fixture: ComponentFixture<EventsInCountriesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EventsInCountriesComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EventsInCountriesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
