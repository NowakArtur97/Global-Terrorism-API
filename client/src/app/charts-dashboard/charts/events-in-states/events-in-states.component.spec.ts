import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventsInStatesComponent } from './events-in-states.component';

describe('EventsInStatesComponent', () => {
  let component: EventsInStatesComponent;
  let fixture: ComponentFixture<EventsInStatesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EventsInStatesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EventsInStatesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
