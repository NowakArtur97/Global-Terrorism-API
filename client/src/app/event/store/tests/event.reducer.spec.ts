import City from 'src/app/city/models/city.model';
import Country from 'src/app/country/models/country.model';
import Province from 'src/app/province/models/province.model';
import Target from 'src/app/target/models/target.model';

import Victim from '../../../victim/models/victim.model';
import Event from '../../models/event.model';
import * as EventActions from '../event.actions';
import eventReducer from '../event.reducer';
import EventStoreState from '../event.state';

const events = [
  new Event(
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
  ),
  new Event(
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
  ),
];

const initialState: EventStoreState = {
  events: [],
};

const initialStateWithEvents: EventStoreState = {
  events,
};

describe('eventReducer', () => {
  describe('EventActions.setEvents', () => {
    it('should store events', () => {
      const action = EventActions.setEvents({ events });
      const actualState = eventReducer(initialState, action);
      const expectedState = { ...initialStateWithEvents };

      expect(actualState).toEqual(expectedState);
      expect(actualState.events.length).toBe(2);
    });

    it('should store empty events list', () => {
      const emptyEventsList = [];
      const action = EventActions.setEvents({ events: emptyEventsList });
      const actualState = eventReducer(initialState, action);
      const expectedState = { ...initialState, events: emptyEventsList };

      expect(actualState).toEqual(expectedState);
      expect(actualState.events.length).toBe(0);
    });
  });

  describe('EventActions.resetEvents', () => {
    it('should reset events list', () => {
      const action = EventActions.resetEvents();
      const actualState = eventReducer(initialStateWithEvents, action);
      const expectedState = { ...initialState };

      expect(actualState).toEqual(expectedState);
      expect(actualState.events.length).toBe(0);
    });
  });
});
