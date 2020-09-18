import { Component, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Subscription } from 'rxjs';
import { map } from 'rxjs/operators';

import AppStoreState from '../store/app.store.state';
import City from './models/city.model';
import * as CitiesActions from './store/cities.actions';

@Component({
  selector: 'app-cities',
  templateUrl: './cities.component.html',
  styleUrls: ['./cities.component.css'],
})
export class CitiesComponent implements OnInit, OnDestroy {
  cities: City[] = [];
  citiesSubscription: Subscription;

  constructor(private store: Store<AppStoreState>) {}

  ngOnInit(): void {
    this.citiesSubscription = this.store
      .select('cities')
      .pipe(map((citiesState) => citiesState.cities))
      .subscribe((cities: City[]) => {
        console.log(cities);
        this.cities = cities;
      });
  }

  ngOnDestroy() {
    this.citiesSubscription.unsubscribe();
  }

  onFetchCities() {
    this.store.dispatch(CitiesActions.fetchCitites());
  }
}
