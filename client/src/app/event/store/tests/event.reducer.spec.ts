import { Dictionary } from '@ngrx/entity';
import City from 'src/app/city/models/city.model';
import Country from 'src/app/country/models/country.model';
import Province from 'src/app/province/models/province.model';
import Target from 'src/app/target/models/target.model';

import Victim from '../../../victim/models/victim.model';
import Event from '../../models/event.model';
import * as EventActions from '../event.actions';
import eventReducer, { EventStoreState } from '../event.reducer';

const event1 = new Event(
  6,
  'summary',
  'motive',
  new Date(),
  false,
  false,
  false,
  new Target(3, 'target', new Country(1, 'country')),
  new City(
    4,
    'city',
    20,
    10,
    new Province(2, 'province', new Country(1, 'country'))
  ),
  new Victim(5, 11, 3, 14, 4, 2000)
);
const event2 = new Event(
  12,
  'summary 2',
  'motive 2',
  new Date(),
  false,
  false,
  false,
  new Target(9, 'target 2', new Country(1, 'country 2')),
  new City(
    10,
    'city 2',
    20,
    10,
    new Province(8, 'province 2', new Country(7, 'country 2'))
  ),
  new Victim(11, 21, 13, 11, 1, 2200)
);
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
};

const initialStateWithEvents: EventStoreState = {
  ids: [6, 12],
  entities: eventsDictionary,
  lastUpdatedEvent: null,
  eventToUpdate: null,
};

const initialStateWithOneEvent: EventStoreState = {
  ids: [6],
  entities: eventsDictionaryWithOneEvent,
  eventToUpdate: null,
  lastUpdatedEvent: null,
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
