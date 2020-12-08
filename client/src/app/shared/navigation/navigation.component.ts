import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatSidenav } from '@angular/material/sidenav';
import { Store } from '@ngrx/store';
import { Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import { AuthenticationComponent } from 'src/app/auth/authentication/authentication.component';
import { RegistrationComponent } from 'src/app/auth/registration/registration.component';
import { selectEventToUpdate } from 'src/app/event/store/event.reducer';
import AppStoreState from 'src/app/store/app.state';

import * as AuthActions from '../../auth/store/auth.actions';
import * as CityActions from '../../city/store/city.actions';
import * as EventActions from '../../event/store/event.actions';

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css'],
})
export class NavigationComponent implements OnInit, OnDestroy {
  private userSubscription$: Subscription;
  private updateSubscription$: Subscription;
  isAuthenticated = false;

  @ViewChild('sidenavForms') sidenavFormElement: MatSidenav;

  constructor(private dialog: MatDialog, private store: Store<AppStoreState>) {}

  ngOnInit(): void {
    this.userSubscription$ = this.store
      .select('auth')
      .pipe(map((authState) => authState.user))
      .subscribe((user) => {
        this.isAuthenticated = !!user;
        if (this.isAuthenticated) {
          this.dialog.closeAll();
        } else {
          this.sidenavFormElement?.close();
        }
      });

    this.updateSubscription$ = this.store
      .select(selectEventToUpdate)
      .subscribe((eventToUpdate) => {
        if (eventToUpdate) {
          this.sidenavFormElement?.open();
        }
      });
  }

  ngOnDestroy(): void {
    this.userSubscription$.unsubscribe();
    this.updateSubscription$.unsubscribe();
  }

  onOpenPopUp(type: string): void {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;

    switch (type) {
      case 'login': {
        this.store.dispatch(AuthActions.startFillingOutForm());
        this.dialog.open(AuthenticationComponent, dialogConfig);
        break;
      }
      case 'registration': {
        this.store.dispatch(AuthActions.startFillingOutForm());
        this.dialog.open(RegistrationComponent, dialogConfig);
        break;
      }
    }
  }

  onLogout(): void {
    this.store.dispatch(AuthActions.logoutUser());
    this.store.dispatch(CityActions.resetCities());
    this.store.dispatch(EventActions.resetEvents());
  }

  onOpenSidenav(): void {
    this.store.dispatch(EventActions.startFillingOutForm());
  }
}
