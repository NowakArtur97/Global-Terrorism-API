import { Action, createReducer, on } from '@ngrx/store';

import * as EventActions from './event.actions';
import EventStoreState from './event.store.state';

const initialState: EventStoreState = {
  events: [],
};

const _eventReducer = createReducer(
  initialState,
  on(EventActions.setEvents, (state, action) => ({
    ...state,
    events: [...action.events],
  })),

  on(EventActions.resetEvents, (state) => ({
    ...state,
    events: [],
  }))
);

export default function eventReducer(
  state: EventStoreState,
  action: Action
): EventStoreState {
  return _eventReducer(state, action);
}
