import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from 'src/app/common/material.module';

import { ProvinceFormComponent } from './province-form.component';

describe('ProvinceFormComponent', () => {
  let component: ProvinceFormComponent;
  let fixture: ComponentFixture<ProvinceFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ProvinceFormComponent],
      imports: [ReactiveFormsModule, MaterialModule, BrowserAnimationsModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProvinceFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    component.ngOnInit();
  });

  describe('form validation', () => {
    it('with empty name should be invalid', () => {
      component.name.setValue('');

      const name = component.name;
      const errors = name.errors;
      expect(errors.notBlank).toBeTruthy();
    });

    it('with blank name should be invalid', () => {
      component.name.setValue('    ');

      const name = component.name;
      const errors = name.errors;
      expect(errors.notBlank).toBeTruthy();
    });
  });
});
