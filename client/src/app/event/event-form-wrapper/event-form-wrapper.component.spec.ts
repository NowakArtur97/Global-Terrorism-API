import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventFormWrapperComponent } from './event-form-wrapper.component';

describe('EventFormWrapperComponent', () => {
  let component: EventFormWrapperComponent;
  let fixture: ComponentFixture<EventFormWrapperComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EventFormWrapperComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EventFormWrapperComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
