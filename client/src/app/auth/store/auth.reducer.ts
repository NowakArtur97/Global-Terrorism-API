import { Action, ActionReducer, createReducer, on } from '@ngrx/store';

import * as AuthActions from './auth.actions';
import AuthStoreState from './auth.store.state';

const initialState: AuthStoreState = {
  user: null,
  authErrorMessages: [],
  isLoading: false,
};

const _authReducer: ActionReducer<AuthStoreState, Action> = createReducer(
  initialState,
  on(AuthActions.loginUserStart, (state) => ({
    ...state,
    authErrorMessages: [],
    isLoading: true,
  })),

  on(AuthActions.registerUserStart, (state) => ({
    ...state,
    isLoading: true,
  })),

  on(AuthActions.authenticateUserSuccess, (state, action) => ({
    ...state,
    user: action.user,
    authErrorMessages: [],
    isLoading: false,
  })),

  on(AuthActions.authenticateUserFailure, (state, action) => ({
    ...state,
    authErrorMessages: action.authErrorMessages,
    isLoading: true,
  })),

  on(AuthActions.logoutUser, (state) => ({
    ...state,
    user: null,
    authErrorMessages: [],
  }))
);

export default function authReducer(
  state: AuthStoreState,
  action: Action
): AuthStoreState {
  return _authReducer(state, action);
}
