import { Dictionary } from '@ngrx/entity';

import Event from '../../models/event.model';
import * as EventActions from '../event.actions';
import eventReducer, { EventStoreState } from '../event.reducer';

const event1 = {
  id: 6,
  summary: 'summary',
  motive: 'motive',
  date: new Date(),
  isPartOfMultipleIncidents: false,
  isSuccessful: true,
  isSuicidal: false,
  target: {
    id: 3,
    target: 'target',
    countryOfOrigin: { id: 1, name: 'country' },
  },
  city: {
    id: 4,
    name: 'city',
    latitude: 20,
    longitude: 10,
    province: {
      id: 2,
      name: 'province',
      country: { id: 1, name: 'country' },
    },
  },
  victim: {
    id: 5,
    totalNumberOfFatalities: 11,
    numberOfPerpetratorFatalities: 3,
    totalNumberOfInjured: 14,
    numberOfPerpetratorInjured: 4,
    valueOfPropertyDamage: 2000,
  },
};
const event2 = {
  id: 12,
  summary: 'summary 2',
  motive: 'motive 2',
  date: new Date(),
  isPartOfMultipleIncidents: true,
  isSuccessful: false,
  isSuicidal: true,
  target: {
    id: 9,
    target: 'target 2',
    countryOfOrigin: { id: 7, name: 'country 2' },
  },
  city: {
    id: 10,
    name: 'city 2',
    latitude: 10,
    longitude: 20,
    province: {
      id: 8,
      name: 'province 2',
      country: { id: 7, name: 'country 2' },
    },
  },
  victim: {
    id: 11,
    totalNumberOfFatalities: 10,
    numberOfPerpetratorFatalities: 2,
    totalNumberOfInjured: 11,
    numberOfPerpetratorInjured: 6,
    valueOfPropertyDamage: 7000,
  },
};

const events: Event[] = [event1, event2];
const eventsDictionary: Dictionary<Event> = {
  6: event1,
  12: event2,
};

const eventsDictionaryWithOneEvent: Dictionary<Event> = {
  6: event1,
};

const initialState: EventStoreState = {
  ids: [],
  entities: {},
  eventToUpdate: null,
  lastUpdatedEvent: null,
  isLoading: false,
};

const initialStateWithEvents: EventStoreState = {
  ids: [6, 12],
  entities: eventsDictionary,
  lastUpdatedEvent: null,
  eventToUpdate: null,
  isLoading: false,
};

const initialStateWithOneEvent: EventStoreState = {
  ids: [6],
  entities: eventsDictionaryWithOneEvent,
  eventToUpdate: null,
  lastUpdatedEvent: null,
  isLoading: false,
};

describe('eventReducer', () => {
  describe('EventActions.setEvents', () => {
    it('should store events', () => {
      const action = EventActions.setEvents({ events });
      const actualState = eventReducer(initialState, action);
      const expectedState = { ...initialStateWithEvents };
      expect(actualState).toEqual(expectedState);
      expect(actualState.ids.length).toBe(2);
    });

    it('should store empty events list', () => {
      const emptyEventsList = [];
      const action = EventActions.setEvents({ events: emptyEventsList });
      const actualState = eventReducer(initialState, action);
      const expectedState = { ...initialState, entities: {} };

      expect(actualState).toEqual(expectedState);
      expect(actualState.ids.length).toBe(0);
    });
  });

  describe('EventActions.resetEvents', () => {
    it('should reset events list', () => {
      const action = EventActions.resetEvents();
      const actualState = eventReducer(initialStateWithEvents, action);
      const expectedState = { ...initialState };

      expect(actualState).toEqual(expectedState);
      expect(actualState.ids.length).toBe(0);
    });
  });

  describe('EventActions.addEvent', () => {
    it('should store event', () => {
      const action = EventActions.addEvent({ event: event1 });
      const actualState = eventReducer(initialState, action);
      const expectedState = { ...initialStateWithOneEvent };
      expect(actualState).toEqual(expectedState);
      expect(actualState.ids.length).toBe(1);
    });
  });
});
