import { BreakpointObserver, LayoutModule } from '@angular/cdk/layout';
import { OverlayModule } from '@angular/cdk/overlay';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { Store, StoreModule } from '@ngrx/store';
import { of } from 'rxjs';
import AuthService from 'src/app/auth/services/auth.service';
import AppStoreState from 'src/app/store/app.store.state';

import * as AuthActions from '../../auth/store/auth.actions';
import * as CitiesActions from '../../cities/store/city.actions';
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

        RouterTestingModule,
        HttpClientTestingModule,
        StoreModule.forRoot({}),
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
    it('should dispatch logoutUser and resetCities actions', () => {
      component.onLogout();

      expect(store.dispatch).toHaveBeenCalledWith(AuthActions.logoutUser());
      expect(store.dispatch).toHaveBeenCalledWith(CitiesActions.resetCities());
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
