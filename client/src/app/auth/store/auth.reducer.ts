import { Action, createReducer, on } from '@ngrx/store';

import * as AuthActions from './auth.actions';
import AuthStoreState from './auth.store.state';

const initialState: AuthStoreState = {
  user: null,
};

const _authReducer = createReducer(
  initialState,
  on(AuthActions.loginUserStart, (state, action) => ({
    ...state,
  })),

  on(AuthActions.authenticateUserSuccess, (state, action) => ({
    ...state,
    user: action.user,
  })),

  on(AuthActions.logoutUser, (state, action) => ({
    ...state,
    user: null,
  }))
);

export default function authReducer(state: AuthStoreState, action: Action) {
  return _authReducer(state, action);
}
