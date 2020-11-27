import { Dictionary } from '@ngrx/entity';

import EventDTO from '../../models/event.dto';
import Event from '../../models/event.model';
import * as EventActions from '../event.actions';
import eventReducer, {
  EventStoreState,
  selectAllEventsBeforeDate,
} from '../event.reducer';

const maxDate = new Date();
const event1 = {
  id: 6,
  summary: 'summary',
  motive: 'motive',
  date: new Date(1999, 6, 12),
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
  date: new Date(1999, 2, 3),
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
const date =
  event1.date.getFullYear() +
  '-' +
  (event1.date.getMonth() + 1) +
  '-' +
  event1.date.getDate();
const eventDTO: EventDTO = {
  id: 6,
  summary: 'summary',
  motive: 'motive',
  date,
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
const eventsDictionary: Dictionary<Event> = {
  6: event1,
  12: event2,
};

const state: EventStoreState = {
  ids: [],
  entities: {},
  eventToUpdate: null,
  lastUpdatedEvent: null,
  lastDeletedEvent: null,
  isLoading: false,
  maxDate,
};
const stateWithEvents: EventStoreState = {
  ids: [6, 12],
  entities: eventsDictionary,
  eventToUpdate: null,
  lastUpdatedEvent: null,
  lastDeletedEvent: null,
  isLoading: false,
  maxDate,
};
const stateWithEventToUpdate: EventStoreState = {
  ids: [],
  entities: {},
  eventToUpdate: event1,
  lastUpdatedEvent: null,
  lastDeletedEvent: null,
  isLoading: false,
  maxDate,
};

describe('eventReducer', () => {
  describe('EventActions.setEvents', () => {
    it('should store events', () => {
      const events: Event[] = [event1, event2];
      const action = EventActions.setEvents({ events });
      const actualState = eventReducer(state, action);
      const expectedState = { ...stateWithEvents };

      expect(actualState).toEqual(expectedState);
    });

    it('should store empty events list', () => {
      const emptyEventsList = [];
      const action = EventActions.setEvents({ events: emptyEventsList });
      const actualState = eventReducer(state, action);
      const expectedState = { ...state, entities: {} };

      expect(actualState).toEqual(expectedState);
    });
  });

  describe('EventActions.resetEvents', () => {
    it('should reset events list', () => {
      const action = EventActions.resetEvents();
      const actualState = eventReducer(stateWithEvents, action);
      const expectedState = { ...state };

      expect(actualState).toEqual(expectedState);
    });
  });

  describe('EventActions.addEventStart', () => {
    it('should start loading', () => {
      const stateWhenAddEventStart: EventStoreState = {
        ids: [],
        entities: {},
        eventToUpdate: null,
        lastUpdatedEvent: null,
        lastDeletedEvent: null,
        isLoading: true,
        maxDate,
      };
      const action = EventActions.addEventStart({ eventDTO });
      const actualState = eventReducer(state, action);
      const expectedState = { ...stateWhenAddEventStart };

      expect(actualState).toEqual(expectedState);
    });
  });

  describe('EventActions.addEvent', () => {
    it('should store event', () => {
      const stateWhenAddEventStart: EventStoreState = {
        ids: [],
        entities: {},
        eventToUpdate: null,
        lastUpdatedEvent: null,
        lastDeletedEvent: null,
        isLoading: false,
        maxDate,
      };
      const eventsDictionaryWithOneEvent: Dictionary<Event> = {
        6: event1,
      };
      const stateWithOneEvent: EventStoreState = {
        ids: [6],
        entities: eventsDictionaryWithOneEvent,
        eventToUpdate: null,
        lastUpdatedEvent: null,
        lastDeletedEvent: null,
        isLoading: false,
        maxDate,
      };
      const action = EventActions.addEvent({ event: event1 });
      const actualState = eventReducer(stateWhenAddEventStart, action);
      const expectedState = { ...stateWithOneEvent };

      expect(actualState).toEqual(expectedState);
    });
  });

  describe('EventActions.updateEventStart', () => {
    it('should reset previous event to update', () => {
      const action = EventActions.updateEventStart({ id: event1.id });
      const actualState = eventReducer(stateWithEventToUpdate, action);
      const expectedState = { ...state };

      expect(actualState).toEqual(expectedState);
    });
  });

  describe('EventActions.updateEventFetch', () => {
    it('should store event to update', () => {
      const stateWhenUpdateEventStart: EventStoreState = {
        ids: [],
        entities: {},
        eventToUpdate: null,
        lastUpdatedEvent: null,
        lastDeletedEvent: null,
        isLoading: false,
        maxDate,
      };
      const action = EventActions.updateEventFetch({ eventToUpdate: event1 });
      const actualState = eventReducer(stateWhenUpdateEventStart, action);
      const expectedState = { ...stateWithEventToUpdate };

      expect(actualState).toEqual(expectedState);
    });
  });

  describe('EventActions.updateEvent', () => {
    it('should remove last updated event and start loading', () => {
      const stateWithLastUpdatedEvent: EventStoreState = {
        ids: [],
        entities: {},
        eventToUpdate: null,
        lastUpdatedEvent: event1,
        lastDeletedEvent: null,
        isLoading: false,
        maxDate,
      };
      const stateWhenUpdateEvent: EventStoreState = {
        ids: [],
        entities: {},
        eventToUpdate: null,
        lastUpdatedEvent: null,
        lastDeletedEvent: null,
        isLoading: true,
        maxDate,
      };
      const action = EventActions.updateEvent({ eventDTO });
      const actualState = eventReducer(stateWithLastUpdatedEvent, action);
      const expectedState = { ...stateWhenUpdateEvent };

      expect(actualState).toEqual(expectedState);
    });
  });

  describe('EventActions.updateEventFinish', () => {
    it('should set last updated event and remove event to update', () => {
      const event2Updated = {
        id: 12,
        summary: 'summary 2 ver 2',
        motive: 'motive 2 ver 2',
        date: new Date(),
        isPartOfMultipleIncidents: true,
        isSuccessful: false,
        isSuicidal: true,
        target: {
          id: 9,
          target: 'target 2 ver 2',
          countryOfOrigin: { id: 7, name: 'country 2 ver 2' },
        },
        city: {
          id: 10,
          name: 'city 2 ver 2',
          latitude: 10,
          longitude: 20,
          province: {
            id: 8,
            name: 'province 2 ver 2',
            country: { id: 7, name: 'country 2 ver 2' },
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
      const stateWithEventsUpdated: EventStoreState = {
        ids: [6, 12],
        entities: eventsDictionary,
        eventToUpdate: event2Updated,
        lastUpdatedEvent: null,
        lastDeletedEvent: null,
        isLoading: false,
        maxDate,
      };
      const eventsDictionaryWithUpdatedEvents: Dictionary<Event> = {
        6: event1,
        12: event2Updated,
      };
      const stateWithLastUpdatedEvent: EventStoreState = {
        ids: [6, 12],
        entities: eventsDictionaryWithUpdatedEvents,
        eventToUpdate: null,
        lastUpdatedEvent: event2Updated,
        lastDeletedEvent: null,
        isLoading: false,
        maxDate,
      };
      const action = EventActions.updateEventFinish({
        eventUpdated: event2Updated,
      });
      const actualState = eventReducer(stateWithEventsUpdated, action);
      const expectedState = { ...stateWithLastUpdatedEvent };

      expect(actualState).toEqual(expectedState);
    });
  });

  describe('EventActions.deleteEventStart', () => {
    it('should remove last deleted event and start loading', () => {
      const stateWithLastDeletedEvent: EventStoreState = {
        ids: [],
        entities: {},
        eventToUpdate: null,
        lastUpdatedEvent: null,
        lastDeletedEvent: event1,
        isLoading: false,
        maxDate,
      };
      const stateWhenDeleteEventStart: EventStoreState = {
        ids: [],
        entities: {},
        eventToUpdate: null,
        lastUpdatedEvent: null,
        lastDeletedEvent: null,
        isLoading: true,
        maxDate,
      };
      const action = EventActions.deleteEventStart({ eventToDelete: event1 });
      const actualState = eventReducer(stateWithLastDeletedEvent, action);
      const expectedState = { ...stateWhenDeleteEventStart };

      expect(actualState).toEqual(expectedState);
    });
  });

  describe('EventActions.deleteEvent', () => {
    it('should set last deleted event and stop loading', () => {
      const stateWhenDeleteEvent: EventStoreState = {
        ids: [],
        entities: {},
        eventToUpdate: null,
        lastUpdatedEvent: null,
        lastDeletedEvent: null,
        isLoading: true,
        maxDate,
      };
      const stateAfterDeletingEvent: EventStoreState = {
        ids: [],
        entities: {},
        eventToUpdate: null,
        lastUpdatedEvent: null,
        lastDeletedEvent: event1,
        isLoading: false,
        maxDate,
      };
      const action = EventActions.deleteEvent({ eventDeleted: event1 });
      const actualState = eventReducer(stateWhenDeleteEvent, action);
      const expectedState = { ...stateAfterDeletingEvent };

      expect(actualState).toEqual(expectedState);
    });
  });

  describe('EventActions.changeMaxEventsDate', () => {
    it('should set new max date', () => {
      const newMaxDate = new Date(1997, 0, 1);
      const stateAfterChangingMaxDate: EventStoreState = {
        ids: [],
        entities: {},
        eventToUpdate: null,
        lastUpdatedEvent: null,
        lastDeletedEvent: null,
        isLoading: false,
        maxDate: newMaxDate,
      };
      const action = EventActions.changeMaxEventsDate({
        maxDate: newMaxDate,
      });
      const actualState = eventReducer(state, action);
      const expectedState = { ...stateAfterChangingMaxDate };

      expect(actualState).toEqual(expectedState);
    });
  });

  describe('EventsSelectors', () => {
    describe('selectAllEventsBeforeDate', () => {
      it('should select events before max date', () => {
        const event3 = {
          id: 18,
          summary: 'summary 3',
          motive: 'motive 3',
          date: new Date(1999, 2, 2),
          isPartOfMultipleIncidents: false,
          isSuccessful: true,
          isSuicidal: false,
          target: {
            id: 15,
            target: 'target 3',
            countryOfOrigin: { id: 13, name: 'country 3' },
          },
          city: {
            id: 16,
            name: 'city 3',
            latitude: 20,
            longitude: 10,
            province: {
              id: 4,
              name: 'province 3',
              country: { id: 13, name: 'country 3' },
            },
          },
          victim: {
            id: 17,
            totalNumberOfFatalities: 12,
            numberOfPerpetratorFatalities: 1,
            totalNumberOfInjured: 1,
            numberOfPerpetratorInjured: 1,
            valueOfPropertyDamage: 2300,
          },
        };
        const event4 = {
          id: 24,
          summary: 'summary 4',
          motive: 'motive 4',
          date: new Date(1998, 5, 11),
          isPartOfMultipleIncidents: true,
          isSuccessful: false,
          isSuicidal: true,
          target: {
            id: 21,
            target: 'target 4',
            countryOfOrigin: { id: 19, name: 'country 4' },
          },
          city: {
            id: 22,
            name: 'city 4',
            latitude: 30,
            longitude: 40,
            province: {
              id: 20,
              name: 'province 4',
              country: { id: 19, name: 'country 4' },
            },
          },
          victim: {
            id: 23,
            totalNumberOfFatalities: 1,
            numberOfPerpetratorFatalities: 1,
            totalNumberOfInjured: 7,
            numberOfPerpetratorInjured: 6,
            valueOfPropertyDamage: 5000,
          },
        };

        const expectedMaxDate = new Date(1999, 2, 3);

        const events: Event[] = [event1, event2, event3, event4];
        const expctedEvents: Event[] = [event2, event3, event4];

        expect(
          selectAllEventsBeforeDate.projector(events, expectedMaxDate)
        ).toEqual(expctedEvents);
      });
    });
  });
});
