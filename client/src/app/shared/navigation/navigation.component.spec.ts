import { BreakpointObserver, LayoutModule } from '@angular/cdk/layout';
import { OverlayModule } from '@angular/cdk/overlay';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { BrowserAnimationsModule, NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { EffectsModule } from '@ngrx/effects';
import { Store, StoreModule } from '@ngrx/store';
import { of } from 'rxjs';
import AuthService from 'src/app/auth/services/auth.service';
import { AuthStoreState } from 'src/app/auth/store/auth.reducer';
import { CityModule } from 'src/app/city/city.module';
import { MaterialModule } from 'src/app/common/material.module';
import { CountryModule } from 'src/app/country/country.module';
import EventModule from 'src/app/event/event.module';
import { EventStoreState, selectEventToUpdate } from 'src/app/event/store/event.reducer';
import { ProvinceModule } from 'src/app/province/province.module';
import AppStoreState from 'src/app/store/app.state';
import { TargetModule } from 'src/app/target/target.module';
import { VictimModule } from 'src/app/victim/victim.module';

import * as AuthActions from '../../auth/store/auth.actions';
import * as CityActions from '../../city/store/city.actions';
import * as EventActions from '../../event/store/event.actions';
import { NavigationComponent } from './navigation.component';

describe('NavigationComponent', () => {
  let component: NavigationComponent;
  let fixture: ComponentFixture<NavigationComponent>;
  let store: Store<AppStoreState>;
  let dialog: MatDialog;
  const dialogRefSpyObj = jasmine.createSpyObj({
    afterClosed: of({}),
    close: null,
  });
  dialogRefSpyObj.componentInstance = { body: '' };

  const stateWithoutEvent: EventStoreState = {
    ids: [],
    entities: {},
    eventToUpdate: null,
    lastUpdatedEvent: null,
    lastDeletedEvent: null,
    isLoading: false,
    maxDate: new Date(),
    errorMessages: [],
  };

  beforeEach(async () => {
    TestBed.configureTestingModule({
      declarations: [NavigationComponent],
      imports: [
        NoopAnimationsModule,
        LayoutModule,
        MatButtonModule,
        MatIconModule,
        MatListModule,
        MatSidenavModule,
        MatToolbarModule,
        OverlayModule,
        MatDialogModule,

        StoreModule.forRoot({}),
        ReactiveFormsModule,
        MaterialModule,
        BrowserAnimationsModule,
        EffectsModule.forRoot(),
        HttpClientTestingModule,
        RouterTestingModule,

        MatSidenavModule,
        EventModule,
        VictimModule,
        TargetModule,
        CityModule,
        ProvinceModule,
        CountryModule,
      ],
      providers: [Store, BreakpointObserver, MatDialog, AuthService],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(NavigationComponent);
    component = fixture.componentInstance;

    store = TestBed.inject(Store);
    dialog = TestBed.inject(MatDialog);
  });

  describe('when initialize component', () => {
    beforeEach(async () => {
      spyOn(store, 'select').and.callFake((selector) => {
        if (selector === 'auth') {
          return of([]);
        } else if (selector === 'event') {
          return of(stateWithoutEvent);
        } else if (selector === selectEventToUpdate) {
          return of(stateWithoutEvent.eventToUpdate);
        }
      });

      fixture.detectChanges();
      component.ngOnInit();
    });

    it('should select user from store', () => {
      expect(store.select).toHaveBeenCalled();
    });
  });

  describe('when user is logged in', () => {
    const eventToUpdate = {
      id: 6,
      summary: 'summary',
      motive: 'motive',
      date: new Date(),
      isPartOfMultipleIncidents: false,
      isSuccessful: true,
      isSuicidal: false,
      target: {
        id: 3,
        target: 'target',
        countryOfOrigin: { id: 1, name: 'country' },
      },
      city: {
        id: 4,
        name: 'city',
        latitude: 20,
        longitude: 10,
        province: {
          id: 2,
          name: 'province',
          country: { id: 1, name: 'country' },
        },
      },
      victim: {
        id: 5,
        totalNumberOfFatalities: 11,
        numberOfPerpetratorsFatalities: 3,
        totalNumberOfInjured: 14,
        numberOfPerpetratorsInjured: 4,
        valueOfPropertyDamage: 2000,
      },
    };
    const stateWithEvent: EventStoreState = {
      ids: [],
      entities: {},
      eventToUpdate,
      lastUpdatedEvent: null,
      lastDeletedEvent: null,
      isLoading: false,
      maxDate: new Date(),
      errorMessages: [],
    };
    const user = {
      token: 'token',
      expirationDate: new Date(new Date().getMilliseconds() + 3600000),
    };
    const stateWithUser: AuthStoreState = {
      user,
      authErrorMessages: [],
      isLoading: false,
    };

    beforeEach(async () => {
      spyOn(store, 'dispatch');
      spyOn(store, 'select').and.callFake((selector) => {
        if (selector === 'auth') {
          return of(stateWithUser);
        } else if (selector === 'event') {
          return of(stateWithEvent);
        } else if (selector === selectEventToUpdate) {
          return of(stateWithEvent.eventToUpdate);
        }
      });
      spyOn(TestBed.inject(MatDialog), 'closeAll').and.returnValue(
        dialogRefSpyObj
      );

      fixture.detectChanges();
      component.ngOnInit();
    });

    describe('and is event to update', () => {
      it('should open side nav', () => {
        expect(component.sidenavFormElement.opened).toBeTruthy();
      });
    });

    it('should close login and register pop ups', () => {
      expect(dialog.closeAll).toHaveBeenCalled();
    });

    describe('is logging out', () => {
      it('should dispatch logoutUser, resetCities and resetEvents actions', () => {
        component.onLogout();

        expect(store.dispatch).toHaveBeenCalledWith(AuthActions.logoutUser());
        expect(store.dispatch).toHaveBeenCalledWith(CityActions.resetCities());
        expect(store.dispatch).toHaveBeenCalledWith(EventActions.resetEvents());
      });
    });
  });

  describe('when user is logged out', () => {
    beforeEach(async () => {
      spyOn(store, 'select').and.callFake((selector) => {
        if (selector === 'auth') {
          return of([]);
        } else if (selector === 'event') {
          return of(stateWithoutEvent);
        } else if (selector === selectEventToUpdate) {
          return of(stateWithoutEvent.eventToUpdate);
        }
      });
      spyOn(store, 'dispatch');
      spyOn(TestBed.inject(MatDialog), 'open').and.returnValue(dialogRefSpyObj);

      fixture.detectChanges();
      component.ngOnInit();
    });

    it('should side nav with form be closed', () => {
      expect(component.sidenavFormElement.opened).toBeFalsy();
    });

    describe('is opening popup', () => {
      it('with login option should open login component and dispatch startFillingOutForm action', () => {
        component.onOpenPopUp('login');

        expect(store.dispatch).toHaveBeenCalledWith(
          AuthActions.startFillingOutForm()
        );
        expect(dialog.open).toHaveBeenCalled();
      });

      it('with registration option should open registration component and dispatch startFillingOutForm action', () => {
        component.onOpenPopUp('registration');

        expect(store.dispatch).toHaveBeenCalledWith(
          AuthActions.startFillingOutForm()
        );
        expect(dialog.open).toHaveBeenCalled();
      });
    });
  });
});
