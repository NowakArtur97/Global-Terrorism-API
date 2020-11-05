import { Update } from '@ngrx/entity';
import { createAction, props } from '@ngrx/store';

import EventDTO from '../models/event.dto';
import Event from '../models/event.model';

export const setEvents = createAction(
  '[Event] Set Events',
  props<{
    events: Event[];
  }>()
);

export const resetEvents = createAction('[Event] Reset Events');

export const fetchEvents = createAction('[Event] Fetch Events');

export const addEventStart = createAction(
  '[Event] Add Event Start',
  props<{ event: EventDTO }>()
);

export const addEvent = createAction(
  '[Event] Add Event',
  props<{ event: Event }>()
);

export const updateEventStart = createAction(
  '[Event] Update Event Start',
  props<{ eventToUpdate: Event }>()
);

export const updateEvent = createAction(
  '[Event] Update Event',
  props<{ event: Update<Event> }>()
);
