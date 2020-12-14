import { createEntityAdapter, EntityState } from '@ngrx/entity';
import {
  Action,
  createFeatureSelector,
  createReducer,
  createSelector,
  on,
} from '@ngrx/store';
import { selectUserLocation } from 'src/app/auth/store/auth.reducer';

import Event from '../models/event.model';
import * as EventActions from './event.actions';

export interface EventStoreState extends EntityState<Event> {
  eventToUpdate: Event;
  lastUpdatedEvent: Event;
  lastDeletedEvent: Event;
  isLoading: boolean;
  endDateOfEvents: Date;
  maxRadiusOfEventsDetection: number;
  errorMessages: string[];
}

const eventAdapter = createEntityAdapter<Event>();

const initialState = eventAdapter.getInitialState({
  eventToUpdate: null,
  lastUpdatedEvent: null,
  lastDeletedEvent: null,
  isLoading: false,
  endDateOfEvents: new Date(),
  maxRadiusOfEventsDetection: null,
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

  on(EventActions.changeEndDateOfEvents, (state, { endDateOfEvents }) => {
    return { ...state, endDateOfEvents };
  }),

  on(
    EventActions.changeMaxRadiusOfEventsDetection,
    (state, { maxRadiusOfEventsDetection }) => {
      return { ...state, maxRadiusOfEventsDetection };
    }
  )
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
const getEndDateOfEvents = (state: EventStoreState) => state.endDateOfEvents;
const getMaxRadiusOfEventsDetection = (state: EventStoreState) =>
  state.maxRadiusOfEventsDetection;

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
export const selectEndDateOfEvents = createSelector(
  selectEventState,
  getEndDateOfEvents
);
export const selectAllEventsBeforeDate = createSelector(
  selectAllEvents,
  selectEndDateOfEvents,
  (events: Event[], endDateOfEvents: Date) =>
    events.filter((event: Event) => new Date(event.date) <= endDateOfEvents)
);
export const selectMaxRadiusOfEventsDetection = createSelector(
  selectEventState,
  getMaxRadiusOfEventsDetection
);
export const selectAllEventsInRadius = createSelector(
  selectAllEventsBeforeDate,
  selectMaxRadiusOfEventsDetection,
  selectUserLocation,
  (
    events: Event[],
    maxRadiusOfEventsDetection: number,
    userLocation: L.LatLngExpression
  ) => {
    if (events.length > 0 && maxRadiusOfEventsDetection && userLocation) {
      const toRadian = (degree) => (degree * Math.PI) / 180;
      const latUser = toRadian(userLocation[0]);
      const longUser = toRadian(userLocation[1]);
      const EARTH_RADIUS = 6371;
      const EARTH_RADIUS_IN_METERES = EARTH_RADIUS * 1000;

      return events.filter((event: Event) => {
        const latEvent = toRadian(event.city.latitude);
        const longEvent = toRadian(event.city.longitude);
        const deltaLat = latUser - latEvent;
        const deltaLon = longUser - longEvent;

        const a =
          Math.pow(Math.sin(deltaLat / 2), 2) +
          Math.cos(latEvent) *
            Math.cos(latUser) *
            Math.pow(Math.sin(deltaLon / 2), 2);
        const c = 2 * Math.asin(Math.sqrt(a));
        console.log(c * EARTH_RADIUS_IN_METERES);
        return maxRadiusOfEventsDetection >= c * EARTH_RADIUS_IN_METERES;
      });
    }
  }
);
