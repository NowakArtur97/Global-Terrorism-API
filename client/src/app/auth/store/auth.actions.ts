import { createAction, props } from '@ngrx/store';

import User from '../models/User';

export const loginUser = createAction(
  '[User] Login User',
  props<{ user: User }>()
);

export const logoutUser = createAction('[User] Logout User');
