import City from 'src/app/city/models/city.model';

import Event from '../../models/event.model';
import Victim from '../../models/victim.model';
import * as EventActions from '../event.actions';
import eventReducer from '../event.reducer';
import EventStoreState from '../event.store.state';

const events = [
  new Event(
    3,
    'summary',
    'motive',
    new Date(),
    true,
    true,
    true,
    new City(1, 'city', 10, 30),
    new Victim(2, 10, 1, 12, 2, 1000)
  ),
  new Event(
    6,
    'summary 2',
    'motive 2',
    new Date(),
    false,
    false,
    false,
    new City(4, 'city 2', 20, 10),
    new Victim(5, 11, 3, 14, 4, 2000)
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
