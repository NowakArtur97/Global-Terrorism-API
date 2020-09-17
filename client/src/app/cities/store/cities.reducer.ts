import { Action, createReducer, on } from '@ngrx/store';

import * as CityActions from './cities.actions';
import CitiesStoreState from './cities.store.state';

const initialState: CitiesStoreState = {
  cities: [],
};

const _citiesReducer = createReducer(
  initialState,
  on(CityActions.setCities, (state, action) => ({
    ...state,
    cities: [...action.citites],
  }))
);

export default function citiesReducer(state: CitiesStoreState, action: Action) {
  return _citiesReducer(state, action);
}
