import { createAction, props } from '@ngrx/store';

import City from '../models/city.model';

export const setCitites = createAction(
  '[City] Set Citites',
  props<{ citites: City[] }>()
);
