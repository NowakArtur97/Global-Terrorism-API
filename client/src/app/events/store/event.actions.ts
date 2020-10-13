import { createAction, props } from '@ngrx/store';

import Event from '../models/event.model';

export const setEvents = createAction(
  '[City] Set Events',
  props<{
    events: Event[];
  }>()
);

export const resetEvents = createAction('[City] Reset Events');

export const fetchEvents = createAction('[City] Fetch Events');
