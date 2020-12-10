import { createAction, props } from '@ngrx/store';

import LoginData from '../models/login-data.model';
import RegistrationData from '../models/registration-data.model';
import User from '../models/user.model';

export const loginUserStart = createAction(
  '[User] Login User Start',
  props<{ loginData: LoginData }>()
);

export const registerUserStart = createAction(
  '[User] Register User Start',
  props<{ registrationData: RegistrationData }>()
);

export const authenticateUserSuccess = createAction(
  '[User] Authenticate User Success',
  props<{ user: User }>()
);

export const authenticateUserFailure = createAction(
  '[User] Authenticate User Failure',
  props<{
    authErrorMessages: string[];
  }>()
);

export const autoUserLogin = createAction('[User] Auto User Login');

export const logoutUser = createAction('[User] Logout User');

export const startFillingOutForm = createAction(
  '[User] User Started Filling Out Form'
);

export const setUserLocation = createAction(
  '[User] Set User Location',
  props<{
    userLocation: L.LatLngExpression;
  }>()
);
