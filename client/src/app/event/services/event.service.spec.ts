import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { getTestBed, TestBed } from '@angular/core/testing';

import EventDTO from '../models/event.dto';
import Event from '../models/event.model';
import EventsGetResponse from '../models/events-get-response.model';
import EventService from './event.service';

describe('eventsService', () => {
  let injector: TestBed;
  let eventService: EventService;
  let httpMock: HttpTestingController;

  const BASE_URL = 'http://localhost:8080/api/v1/events';
  const DEFAULT_PAGE_SIZE = 200;
  const DEFAULT_DEPTH = 2;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [EventService],
    });
  });

  beforeEach(() => {
    injector = getTestBed();
    eventService = injector.inject(EventService);
    httpMock = injector.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('when get events', () => {
    it('should return events', () => {
      const events: EventsGetResponse = {
        content: [
          {
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
              numberOfPerpetratorsFatalities: 3,
              totalNumberOfInjured: 14,
              numberOfPerpetratorsInjured: 4,
              valueOfPropertyDamage: 2000,
            },
          },
          {
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
              numberOfPerpetratorsFatalities: 2,
              totalNumberOfInjured: 11,
              numberOfPerpetratorsInjured: 6,
              valueOfPropertyDamage: 7000,
            },
          },
        ],
      };

      eventService.getAll(DEFAULT_PAGE_SIZE, DEFAULT_DEPTH).subscribe((res) => {
        expect(res).toEqual(events);
      });

      const req = httpMock.expectOne(
        `${BASE_URL}/depth/${DEFAULT_DEPTH}?page=0&size=${DEFAULT_PAGE_SIZE}`
      );
      expect(req.request.method).toBe('GET');
      req.flush(events);
    });
  });

  describe('when get event', () => {
    it('should return event', () => {
      const event: Event = {
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
          numberOfPerpetratorsFatalities: 3,
          totalNumberOfInjured: 14,
          numberOfPerpetratorsInjured: 4,
          valueOfPropertyDamage: 2000,
        },
      };

      eventService.get(event.id).subscribe((res) => {
        expect(res).toEqual(event);
      });

      const req = httpMock.expectOne(
        `${BASE_URL}/${event.id}/depth/${DEFAULT_DEPTH}`
      );
      expect(req.request.method).toBe('GET');
      req.flush(event);
    });
  });

  describe('when add event', () => {
    it('should add and return event', () => {
      const event: Event = {
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
          numberOfPerpetratorsFatalities: 3,
          totalNumberOfInjured: 14,
          numberOfPerpetratorsInjured: 4,
          valueOfPropertyDamage: 2000,
        },
      };
      const date =
        event.date.getFullYear() +
        '-' +
        (event.date.getMonth() + 1) +
        '-' +
        event.date.getDate();
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
          numberOfPerpetratorsFatalities: 3,
          totalNumberOfInjured: 14,
          numberOfPerpetratorsInjured: 4,
          valueOfPropertyDamage: 2000,
        },
      };

      eventService.add(eventDTO).subscribe((res) => {
        expect(res).toEqual(event);
      });

      const req = httpMock.expectOne(BASE_URL);
      expect(req.request.method).toBe('POST');
      req.flush(event);
    });
  });

  describe('when update event', () => {
    it('should update and return event', () => {
      const event: Event = {
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
          numberOfPerpetratorsFatalities: 3,
          totalNumberOfInjured: 14,
          numberOfPerpetratorsInjured: 4,
          valueOfPropertyDamage: 2000,
        },
      };
      const date =
        event.date.getFullYear() +
        '-' +
        (event.date.getMonth() + 1) +
        '-' +
        event.date.getDate();
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
          numberOfPerpetratorsFatalities: 3,
          totalNumberOfInjured: 14,
          numberOfPerpetratorsInjured: 4,
          valueOfPropertyDamage: 2000,
        },
      };

      eventService.update(eventDTO).subscribe((res) => {
        expect(res).toEqual(event);
      });

      const req = httpMock.expectOne(`${BASE_URL}/${event.id}`);
      expect(req.request.method).toBe('PUT');
      req.flush(event);
    });
  });

  describe('when delete event', () => {
    it('should delete event', () => {
      const eventId = 1;
      eventService.delete(eventId).subscribe((res) => {
        expect(res).toEqual(null);
      });

      const req = httpMock.expectOne(`${BASE_URL}/${eventId}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });
  });
});
