import { createEntityAdapter, EntityState } from '@ngrx/entity';
import { Action, createFeatureSelector, createReducer, createSelector, on } from '@ngrx/store';

import Event from '../models/event.model';
import * as EventActions from './event.actions';

export interface EventStoreState extends EntityState<Event> {
  eventToUpdate: Event;
  lastUpdatedEvent: Event;
  lastDeletedEvent: Event;
  isLoading: boolean;
  maxDate: Date;
  maxRadius: number;
  errorMessages: string[];
}

const eventAdapter = createEntityAdapter<Event>();

const initialState = eventAdapter.getInitialState({
  eventToUpdate: null,
  lastUpdatedEvent: null,
  lastDeletedEvent: null,
  isLoading: false,
  maxDate: new Date(),
  maxRadius: 10,
  errorMessages: [],
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
    return { ...state, isLoading: true, errorMessages: [] };
  }),

  on(EventActions.addEvent, (state, { event }) => {
    return eventAdapter.addOne(event, {
      ...state,
      isLoading: false,
      errorMessages: [],
    });
  }),

  on(EventActions.updateEventStart, (state) => {
    return {
      ...state,
      eventToUpdate: null,
      lastUpdatedEvent: null,
      errorMessages: [],
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
        errorMessages: [],
      }
    );
  }),

  on(EventActions.deleteEventStart, (state) => {
    return {
      ...state,
      lastDeletedEvent: null,
      isLoading: true,
      errorMessages: [],
    };
  }),

  on(EventActions.deleteEvent, (state, { eventDeleted }) => {
    return eventAdapter.removeOne(eventDeleted.id, {
      ...state,
      lastDeletedEvent: eventDeleted,
      isLoading: false,
      errorMessages: [],
    });
  }),

  on(EventActions.httpError, (state, { errorMessages }) => {
    return { ...state, isLoading: false, errorMessages };
  }),

  on(EventActions.startFillingOutForm, (state) => {
    return { ...state, errorMessages: [] };
  }),

  on(EventActions.changeMaxEventsDate, (state, { maxDate }) => {
    return { ...state, maxDate };
  }),

  on(EventActions.changeMaxEventsDetectionRadius, (state, { maxRadius }) => {
    return { ...state, maxRadius };
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
const getLastDeletedEvent = (state: EventStoreState) => state.lastDeletedEvent;
const getMaxDate = (state: EventStoreState) => state.maxDate;

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
export const selectLastDeletedEvent = createSelector(
  selectEventState,
  getLastDeletedEvent
);
export const selectMaxDate = createSelector(selectEventState, getMaxDate);
export const selectAllEventsBeforeDate = createSelector(
  selectAllEvents,
  selectMaxDate,
  (events: Event[], maxDate: Date) =>
    events.filter((event: Event) => new Date(event.date) <= maxDate)
);
