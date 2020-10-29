import City from 'src/app/city/models/city.model';
import Target from 'src/app/target/models/target.model';

import Victim from '../../../victim/models/victim.model';
import Event from '../../models/event.model';
import * as EventActions from '../event.actions';
import eventReducer from '../event.reducer';
import EventStoreState from '../event.store.state';

const events = [
  new Event(
    4,
    'summary',
    'motive',
    new Date(),
    true,
    true,
    true,
    new Target(1, 'target'),
    new City(2, 'city', 10, 30),
    new Victim(3, 10, 1, 12, 2, 1000)
  ),
  new Event(
    8,
    'summary 2',
    'motive 2',
    new Date(),
    false,
    false,
    false,
    new Target(5, 'target 2'),
    new City(6, 'city 2', 20, 10),
    new Victim(7, 11, 3, 14, 4, 2000)
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
