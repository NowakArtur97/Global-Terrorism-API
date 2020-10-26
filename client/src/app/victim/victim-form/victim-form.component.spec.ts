import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VictimFormComponent } from './victim-form.component';

describe('VictimFormComponent', () => {
  let component: VictimFormComponent;
  let fixture: ComponentFixture<VictimFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VictimFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VictimFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
