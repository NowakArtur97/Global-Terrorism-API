import { createEntityAdapter } from '@ngrx/entity/src';
import { Action, createReducer, on } from '@ngrx/store';

import Event from '../models/event.model';
import * as EventActions from './event.actions';
import EventStoreState from './event.state';

export const eventAdapter = createEntityAdapter<Event>();

const initialState = eventAdapter.getInitialState();

const _eventReducer = createReducer(
  initialState,
  on(EventActions.setEvents, (state, { events }) => {
    return eventAdapter.setAll(events, state);
  }),

  on(EventActions.resetEvents, (state) => {
    return eventAdapter.removeAll(state);
  })
);

export default function eventReducer(
  state: EventStoreState,
  action: Action
): EventStoreState {
  return _eventReducer(state, action);
}

const { selectAll, selectTotal } = eventAdapter.getSelectors();

export const selectAllEvents = selectAll;

export const selectEventsTotal = selectTotal;
