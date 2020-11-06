import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Store, StoreModule } from '@ngrx/store';
import { MaterialModule } from 'src/app/common/material.module';

import { CountryFormComponent } from './country-form.component';

describe('CountryFormComponent', () => {
  let component: CountryFormComponent;
  let fixture: ComponentFixture<CountryFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CountryFormComponent],
      imports: [
        StoreModule.forRoot({}),
        ReactiveFormsModule,
        MaterialModule,
        BrowserAnimationsModule,
      ],
      providers: [Store],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CountryFormComponent);
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
