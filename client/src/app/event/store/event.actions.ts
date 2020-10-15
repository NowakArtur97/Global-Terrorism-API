import { createAction, props } from '@ngrx/store';

import Event from '../models/event.model';

export const setEvents = createAction(
  '[Event] Set Events',
  props<{
    events: Event[];
  }>()
);

export const resetEvents = createAction('[Event] Reset Events');

export const fetchEvents = createAction('[Event] Fetch Events');
