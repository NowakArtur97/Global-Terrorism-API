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
import { EffectsModule } from '@ngrx/effects';
import { Store, StoreModule } from '@ngrx/store';
import { of } from 'rxjs';
import AuthService from 'src/app/auth/services/auth.service';
import { CityModule } from 'src/app/city/city.module';
import { MaterialModule } from 'src/app/common/material.module';
import { CountryModule } from 'src/app/country/country.module';
import { EventFormWrapperComponent } from 'src/app/event/event-form-wrapper/event-form-wrapper.component';
import EventModule from 'src/app/event/event.module';
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

  beforeEach(async () => {
    TestBed.configureTestingModule({
      declarations: [NavigationComponent, EventFormWrapperComponent],
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

    spyOn(store, 'select').and.callFake((selector) => {
      if (selector === 'auth') {
        return of([]);
      }
    });
    spyOn(store, 'dispatch');

    fixture.detectChanges();
    component.ngOnInit();
  });

  describe('when initialize component', () => {
    it('should select user from store', () => {
      expect(store.select).toHaveBeenCalled();
    });
  });

  describe('when logout user', () => {
    it('should dispatch logoutUser, resetCities and resetEvents actions', () => {
      component.onLogout();

      expect(store.dispatch).toHaveBeenCalledWith(AuthActions.logoutUser());
      expect(store.dispatch).toHaveBeenCalledWith(CityActions.resetCities());
      expect(store.dispatch).toHaveBeenCalledWith(EventActions.resetEvents());
    });
  });

  describe('when open popup', () => {
    it('with login option should open login component', () => {
      spyOn(TestBed.inject(MatDialog), 'open').and.returnValue(dialogRefSpyObj);

      component.onOpenPopUp('login');

      expect(dialog.open).toHaveBeenCalled();
    });

    it('with registration option should open registration component', () => {
      spyOn(TestBed.inject(MatDialog), 'open').and.returnValue(dialogRefSpyObj);

      component.onOpenPopUp('registration');

      expect(dialog.open).toHaveBeenCalled();
    });
  });
});
