import { createAction, props } from '@ngrx/store';

import LoginData from '../models/LoginData';

export const loginUser = createAction(
  '[User] Login User',
  props<{ loginData: LoginData }>()
);

export const logoutUser = createAction('[User] Logout User');
