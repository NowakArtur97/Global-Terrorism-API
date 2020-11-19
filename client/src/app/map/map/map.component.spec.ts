import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Store, StoreModule } from '@ngrx/store';
import { of } from 'rxjs';
import { AuthStoreState } from 'src/app/auth/store/auth.reducer';
import { MaterialModule } from 'src/app/common/material.module';
import {
  selectAllEvents,
  selectLastDeletedEvent,
} from 'src/app/event/store/event.reducer';
import AppStoreState from 'src/app/store/app.state';

import * as EventActions from '../../event/store/event.actions';
import { MapComponent } from './map.component';

describe('MapComponent', () => {
  let component: MapComponent;
  let fixture: ComponentFixture<MapComponent>;
  let store: Store<AppStoreState>;
  const stateWithUser: AuthStoreState = {
    user: { token: 'token', expirationDate: new Date(Date.now() + 36000000) },
    authErrorMessages: [],
    isLoading: false,
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MapComponent],
      imports: [
        StoreModule.forRoot({}),
        MaterialModule,
        BrowserAnimationsModule,
      ],
      providers: [Store],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MapComponent);
    component = fixture.componentInstance;

    store = TestBed.inject(Store);
    spyOn(store, 'select').and.callFake((selector) => {
      if (selector === selectAllEvents) {
        return of([]);
      } else if (selector === 'auth') {
        return of(stateWithUser);
      } else if (selector === selectLastDeletedEvent) {
        return of();
      }
    });
    spyOn(store, 'dispatch');

    fixture.detectChanges();
    component.ngOnInit();
  });

  describe('when initialize component', () => {
    it('should select and dispatch events', () => {
      expect(store.select).toHaveBeenCalled();
      expect(store.dispatch).toHaveBeenCalledWith(EventActions.fetchEvents());
    });
  });
});
