import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Store, StoreModule } from '@ngrx/store';
import { of } from 'rxjs';
import AppStoreState from 'src/app/store/app.store.state';

import User from '../../auth/models/user.model';
import AuthStoreState from '../../auth/store/auth.store.state';
import * as EventActions from '../../event/store/event.actions';
import { MapComponent } from './map.component';

describe('MapComponent', () => {
  let component: MapComponent;
  let fixture: ComponentFixture<MapComponent>;
  let store: Store<AppStoreState>;
  const initialStateWithUser: AuthStoreState = {
    user: new User('token', 36000000),
    authErrorMessages: [],
    isLoading: false,
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StoreModule.forRoot({})],
      declarations: [MapComponent],
      providers: [Store],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MapComponent);
    component = fixture.componentInstance;

    store = TestBed.inject(Store);
    spyOn(store, 'select').and.callFake((selector) => {
      if (selector === 'event') {
        return of([]);
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
