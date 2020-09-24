import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import AppStoreState from './store/app.store.state';
import * as AuthActions from './auth/store/auth.actions';
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
  constructor(private store: Store<AppStoreState>) {}

  ngOnInit() {
    this.store.dispatch(AuthActions.autoUserLogin());
  }
}
