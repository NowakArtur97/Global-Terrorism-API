import { ActionReducerMap } from '@ngrx/store';

import citiesReducer from '../cities/store/cities.reducer';
import AppStoreState from './app.store.state';

const appReducer: ActionReducerMap<AppStoreState> = {
  cities: citiesReducer,
};

export default appReducer;
