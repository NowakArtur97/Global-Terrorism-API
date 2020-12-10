import { Action, ActionReducer, createFeatureSelector, createReducer, createSelector, on } from '@ngrx/store';

import User from '../models/user.model';
import * as AuthActions from './auth.actions';

export interface AuthStoreState {
  user: User;
  authErrorMessages: string[];
  isLoading: boolean;
  userLocation: L.LatLngExpression;
}

const initialState: AuthStoreState = {
  user: null,
  authErrorMessages: [],
  isLoading: false,
  userLocation: null,
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
    authErrorMessages: [],
    isLoading: true,
  })),

  on(AuthActions.authenticateUserSuccess, (state, action) => ({
    ...state,
    user: action.user,
    authErrorMessages: [],
    isLoading: false,
  })),

  on(AuthActions.authenticateUserFailure, (state, { authErrorMessages }) => ({
    ...state,
    authErrorMessages,
    isLoading: false,
  })),

  on(AuthActions.logoutUser, (state) => ({
    ...state,
    user: null,
    authErrorMessages: [],
    userLocation: null,
  })),

  on(AuthActions.startFillingOutForm, (state) => ({
    ...state,
    authErrorMessages: [],
  })),
  on(AuthActions.setUserLocation, (state, { userLocation }) => ({
    ...state,
    userLocation,
  }))
);

export default function authReducer(
  state: AuthStoreState,
  action: Action
): AuthStoreState {
  return _authReducer(state, action);
}

const getUserLocation = (state: AuthStoreState) => state.userLocation;

export const selectAuthState = createFeatureSelector<AuthStoreState>('auth');
export const selectUserLocation = createSelector(
  selectAuthState,
  getUserLocation
);
