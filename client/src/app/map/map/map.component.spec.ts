import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { EntityState } from '@ngrx/entity';
import { Store, StoreModule } from '@ngrx/store';
import { of } from 'rxjs';
import { MaterialModule } from 'src/app/common/material.module';
import { selectAllEvents } from 'src/app/event/store/event.reducer';
import AppStoreState from 'src/app/store/app.state';

import User from '../../auth/models/user.model';
import AuthStoreState from '../../auth/store/auth.state';
import * as EventActions from '../../event/store/event.actions';
import { MapComponent } from './map.component';

describe('MapComponent', () => {
  let component: MapComponent;
  let fixture: ComponentFixture<MapComponent>;
  let store: Store<AppStoreState>;
  const initialStateWithUser: AuthStoreState = {
    user: new User('token', new Date(Date.now() + 36000000)),
    authErrorMessages: [],
    isLoading: false,
  };
  const initialStateWithEvents: EntityState<Event> = {
    ids: [],
    entities: {},
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
        const emptyArray: Event[] = [];
        return of(emptyArray);
      }
      if (selector === 'auth') {
        return of(initialStateWithUser);
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
