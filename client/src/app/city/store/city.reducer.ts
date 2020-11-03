import { createEntityAdapter, EntityState } from '@ngrx/entity';
import { Action, createFeatureSelector, createReducer, createSelector, on } from '@ngrx/store';

import City from '../models/city.model';
import * as CityActions from './city.actions';

export interface CityStoreState extends EntityState<City> {}

const cityAdapter = createEntityAdapter<City>();

const initialState = cityAdapter.getInitialState();

const _cityReducer = createReducer(
  initialState,
  on(CityActions.setCities, (state, { cities }) => {
    return cityAdapter.setAll(cities, state);
  }),

  on(CityActions.resetCities, (state) => {
    return cityAdapter.removeAll(state);
  })
);

export default function cityReducer(
  state: CityStoreState,
  action: Action
): CityStoreState {
  return _cityReducer(state, action);
}

const { selectAll } = cityAdapter.getSelectors();

export const selectCityState = createFeatureSelector<CityStoreState>('city');

export const selectAllCities = createSelector(selectCityState, selectAll);
