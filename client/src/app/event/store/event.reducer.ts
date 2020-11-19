import { createEntityAdapter, EntityState } from '@ngrx/entity';
import { Action, createFeatureSelector, createReducer, createSelector, on } from '@ngrx/store';

import Event from '../models/event.model';
import * as EventActions from './event.actions';

export interface EventStoreState extends EntityState<Event> {
  eventToUpdate: Event;
  lastUpdatedEvent: Event;
  lastDeletedEventId: number;
  isLoading: boolean;
}

const eventAdapter = createEntityAdapter<Event>();

const initialState = eventAdapter.getInitialState({
  eventToUpdate: null,
  lastUpdatedEvent: null,
  lastDeletedEventId: null,
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

  on(EventActions.updateEventFinish, (state, { eventUpdated }) => {
    return eventAdapter.updateOne(
      { id: eventUpdated.id, changes: { ...eventUpdated } },
      {
        ...state,
        eventToUpdate: null,
        lastUpdatedEvent: { ...eventUpdated },
        isLoading: false,
      }
    );
  }),

  on(EventActions.deleteEventStart, (state) => {
    return { ...state, isLoading: true };
  }),

  on(EventActions.deleteEvent, (state, { id }) => {
    return eventAdapter.removeOne(id, { ...state, lastDeletedEventId: id });
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
const getLastDeletedEventId = (state: EventStoreState) =>
  state.lastDeletedEventId;

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
export const selectLastDeletedEventId = createSelector(
  selectEventState,
  getLastDeletedEventId
);
