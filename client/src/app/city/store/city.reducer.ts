import { Action, createReducer, on } from '@ngrx/store';

import * as CityActions from './city.actions';
import CityStoreState from './city.store.state';

const initialState: CityStoreState = {
  cities: [],
};

const _cityReducer = createReducer(
  initialState,
  on(CityActions.setCities, (state, action) => ({
    ...state,
    cities: [...action.cities],
  })),

  on(CityActions.resetCities, (state) => ({
    ...state,
    cities: [],
  }))
);

export default function cityReducer(
  state: CityStoreState,
  action: Action
): CityStoreState {
  return _cityReducer(state, action);
}
