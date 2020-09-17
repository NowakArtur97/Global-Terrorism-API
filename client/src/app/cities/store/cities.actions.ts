import { createAction, props } from '@ngrx/store';

import City from '../models/city.model';

export const setCities = createAction(
  '[City] Set Cities',
  props<{
    cities: City[];
  }>()
);

export const fetchCitites = createAction('[City] Fetch Cities');
