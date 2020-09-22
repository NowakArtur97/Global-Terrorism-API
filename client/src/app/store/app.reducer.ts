import { ActionReducerMap } from '@ngrx/store';

import authReducer from '../auth/store/auth.reducer';
import citiesReducer from '../cities/store/cities.reducer';
import AppStoreState from './app.store.state';

const appReducer: ActionReducerMap<AppStoreState> = {
  cities: citiesReducer,
  auth: authReducer,
};

export default appReducer;
