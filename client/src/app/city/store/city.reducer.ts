import { Action, createReducer, on } from '@ngrx/store';

import * as CityActions from './city.actions';
import { State } from './city.store.state';

const initialState: State = {
  cities: [],
};

const _cityReducer = createReducer(
  initialState,
  on(CityActions.setCitites, (state, action) => ({
    ...state,
    cities: [...action.citites],
  }))
);

export default function cityReducer(state: State, action: Action) {
  return _cityReducer(state, action);
}
