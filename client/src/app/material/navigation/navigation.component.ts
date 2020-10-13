import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import { Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import { AuthenticationComponent } from 'src/app/auth/authentication/authentication.component';
import { RegistrationComponent } from 'src/app/auth/registration/registration.component';
import AppStoreState from 'src/app/store/app.store.state';

import * as AuthActions from '../../auth/store/auth.actions';
import * as CitiesActions from '../../cities/store/cities.actions';

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css'],
})
export class NavigationComponent implements OnInit {
  isAuthenticated = false;
  private userSubscription: Subscription;

  constructor(private dialog: MatDialog, private store: Store<AppStoreState>) {}

  ngOnInit(): void {
    this.userSubscription = this.store
      .select('auth')
      .pipe(map((authState) => authState.user))
      .subscribe((user) => {
        this.isAuthenticated = !!user;
        if (this.isAuthenticated) {
          this.dialog.closeAll();
        }
      });
  }

  ngOnDestroy(): void {
    this.userSubscription.unsubscribe();
  }

  onOpenPopUp(type: string): void {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;

    switch (type) {
      case 'login': {
        this.dialog.open(AuthenticationComponent, dialogConfig);
        break;
      }
      case 'registration': {
        this.dialog.open(RegistrationComponent, dialogConfig);
        break;
      }
    }
  }

  onLogout(): void {
    this.store.dispatch(AuthActions.logoutUser());
    this.store.dispatch(CitiesActions.resetCities());
  }
}
