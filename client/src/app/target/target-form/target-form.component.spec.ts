import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Store, StoreModule } from '@ngrx/store';
import { of } from 'rxjs';
import { MaterialModule } from 'src/app/common/material.module';
import { EventStoreState, selectEventToUpdate } from 'src/app/event/store/event.reducer';
import AppStoreState from 'src/app/store/app.state';

import { TargetFormComponent } from './target-form.component';

describe('TargetFormComponent', () => {
  let component: TargetFormComponent;
  let fixture: ComponentFixture<TargetFormComponent>;
  let store: Store<AppStoreState>;
  const initialState: EventStoreState = {
    ids: [],
    entities: {},
    eventToUpdate: null,
    lastUpdatedEvent: null,
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TargetFormComponent],
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
    fixture = TestBed.createComponent(TargetFormComponent);
    component = fixture.componentInstance;

    store = TestBed.inject(Store);

    spyOn(store, 'select').and.callFake((selector) => {
      if (selector === selectEventToUpdate) {
        return of(initialState);
      }
    });

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
