import { BreakpointObserver, LayoutModule } from '@angular/cdk/layout';
import { OverlayModule } from '@angular/cdk/overlay';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { Store, StoreModule } from '@ngrx/store';
import { of } from 'rxjs';
import AppStoreState from 'src/app/store/app.store.state';

import * as AuthActions from '../../auth/store/auth.actions';
import * as CitiesActions from '../../cities/store/cities.actions';
import { NavigationComponent } from './navigation.component';

describe('NavigationComponent', () => {
  let component: NavigationComponent;
  let fixture: ComponentFixture<NavigationComponent>;
  let store: Store<AppStoreState>;
  let breakpointObserver: BreakpointObserver;
  let dialog: MatDialog;

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
      ],
      providers: [Store, BreakpointObserver, MatDialog],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(NavigationComponent);
    component = fixture.componentInstance;

    store = TestBed.inject(Store);
    breakpointObserver = TestBed.inject(BreakpointObserver);
    dialog = TestBed.inject(MatDialog);

    spyOn(store, 'select').and.callFake((selector) => {
      if (selector === 'auth') {
        return of([]);
      }
    });
    spyOn(store, 'dispatch');
    spyOn(dialog, 'open');

    fixture.detectChanges();
    component.ngOnInit();
  });

  describe('when initialize component', () => {
    it('should select user from store', () => {
      expect(store.select).toHaveBeenCalled();
    });
  });

  describe('when logout user', () => {
    it('should dispatch logoutUser and resetCities actions', () => {
      component.onLogout();

      expect(store.dispatch).toHaveBeenCalledWith(AuthActions.logoutUser());
      expect(store.dispatch).toHaveBeenCalledWith(CitiesActions.resetCities());
    });
  });

  describe('when open popup', () => {
    it('with login option should open login component', () => {
      (dialog.open as jasmine.Spy).and.callThrough();

      component.onOpenPopUp('login');

      expect(dialog.open).toHaveBeenCalled();
    });

    it('with registration option should open registration component', () => {
      (dialog.open as jasmine.Spy).and.callThrough();

      component.onOpenPopUp('registration');

      expect(dialog.open).toHaveBeenCalled();
    });
  });
});
