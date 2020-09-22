import { createAction, props } from '@ngrx/store';

import LoginData from '../models/LoginData';
import User from '../models/User';

export const loginUserStart = createAction(
  '[User] Login User Start',
  props<{ loginData: LoginData }>()
);

export const authenticateUserSuccess = createAction(
  '[User] Authenticate User Success',
  props<{ user: User }>()
);

export const logoutUser = createAction('[User] Logout User');
