import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from 'src/app/common/material.module';

import { TargetFormComponent } from './target-form.component';

describe('TargetFormComponent', () => {
  let component: TargetFormComponent;
  let fixture: ComponentFixture<TargetFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TargetFormComponent],
      imports: [ReactiveFormsModule, MaterialModule, BrowserAnimationsModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TargetFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    component.ngOnInit();
  });

  describe('form validation', () => {
    it('with empty target should be invalid', () => {
      component.target.setValue('');

      const target = component.target;
      const errors = target.errors;
      expect(errors.notBlank).toBeTruthy();
    });

    it('with blank target should be invalid', () => {
      component.target.setValue('    ');

      const target = component.target;
      const errors = target.errors;
      expect(errors.notBlank).toBeTruthy();
    });
  });
});
