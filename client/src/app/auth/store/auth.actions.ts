import { createAction, props } from '@ngrx/store';

import LoginData from '../models/LoginData';
import RegistrationData from '../models/RegistrationData';
import User from '../models/User';

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
