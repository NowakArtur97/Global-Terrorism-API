import { createEntityAdapter, EntityState } from '@ngrx/entity';
import { Action, createFeatureSelector, createReducer, createSelector, on } from '@ngrx/store';

import Event from '../models/event.model';
import * as EventActions from './event.actions';

export interface EventStoreState extends EntityState<Event> {
  eventToUpdate: Event;
  lastUpdatedEvent: Event;
  isLoading: boolean;
}

const eventAdapter = createEntityAdapter<Event>();

const initialState = eventAdapter.getInitialState({
  eventToUpdate: null,
  lastUpdatedEvent: null,
  isLoading: false,
});

const _eventReducer = createReducer(
  initialState,
  on(EventActions.setEvents, (state, { events }) => {
    return eventAdapter.setAll(events, state);
  }),

  on(EventActions.resetEvents, (state) => {
    return eventAdapter.removeAll(state);
  }),

  on(EventActions.addEventStart, (state) => {
    return { ...state, isLoading: true };
  }),

  on(EventActions.addEvent, (state, { event }) => {
    return eventAdapter.addOne(event, { ...state, isLoading: false });
  }),

  on(EventActions.updateEventStart, (state) => {
    return {
      ...state,
      eventToUpdate: null,
      lastUpdatedEvent: null,
    };
  }),

  on(EventActions.updateEventFetch, (state, { eventToUpdate }) => {
    return { ...state, eventToUpdate };
  }),

  on(EventActions.updateEvent, (state) => {
    return {
      ...state,
      lastUpdatedEvent: null,
      isLoading: true,
    };
  }),

  on(EventActions.updateEventFinish, (state, { eventToUpdate }) => {
    return eventAdapter.updateOne(
      { id: eventToUpdate.id, changes: { ...eventToUpdate } },
      {
        ...state,
        eventToUpdate: null,
        lastUpdatedEvent: { ...eventToUpdate },
        isLoading: false,
      }
    );
  })
);

export default function eventReducer(
  state: EventStoreState,
  action: Action
): EventStoreState {
  return _eventReducer(state, action);
}

const getEventToUpdate = (state: EventStoreState) => state.eventToUpdate;
const getLastUpdatedEvent = (state: EventStoreState) => state.lastUpdatedEvent;

const { selectAll, selectEntities, selectTotal } = eventAdapter.getSelectors();

export const selectEventState = createFeatureSelector<EventStoreState>('event');

export const selectAllEvents = createSelector(selectEventState, selectAll);
export const selectEventsTotal = createSelector(selectEventState, selectTotal);
export const selectEventEntites = createSelector(
  selectEventState,
  selectEntities
);
export const selectEventToUpdate = createSelector(
  selectEventState,
  getEventToUpdate
);
export const selectLastUpdatedEvent = createSelector(
  selectEventState,
  getLastUpdatedEvent
);
