import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import { Observable, Subscription } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';
import { AuthenticationComponent } from 'src/app/auth/authentication/authentication.component';
import { RegistrationComponent } from 'src/app/auth/registration/registration.component';
import AppStoreState from 'src/app/store/app.store.state';

import * as AuthActions from '../../auth/store/auth.actions';

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css'],
})
export class NavigationComponent implements OnInit, OnDestroy {
  isAuthenticated = false;
  private userSubscription: Subscription;

  isHandset$: Observable<boolean> = this.breakpointObserver
    .observe(Breakpoints.Handset)
    .pipe(
      map((result) => result.matches),
      shareReplay()
    );

  constructor(
    private breakpointObserver: BreakpointObserver,
    private dialog: MatDialog,
    private store: Store<AppStoreState>
  ) {}

  ngOnInit(): void {
    this.userSubscription = this.store
      .select('auth')
      .pipe(map((authState) => authState.user))
      .subscribe((user) => (this.isAuthenticated = !!user));
  }

  ngOnDestroy(): void {
    this.userSubscription.unsubscribe();
  }

  onOpenPopUp(type: string) {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = true;
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

  onLogout() {
    this.store.dispatch(AuthActions.logoutUser());
  }
}
