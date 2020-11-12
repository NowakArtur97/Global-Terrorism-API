import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { getTestBed, TestBed } from '@angular/core/testing';

import EventsGetResponse from '../models/events-get-response.model';
import EventService from './event.service';

describe('eventsService', () => {
  let injector: TestBed;
  let eventService: EventService;
  let httpMock: HttpTestingController;

  const BASE_URL = 'http://localhost:8080/api/v1/events';

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
              numberOfPerpetratorFatalities: 3,
              totalNumberOfInjured: 14,
              numberOfPerpetratorInjured: 4,
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
              numberOfPerpetratorFatalities: 2,
              totalNumberOfInjured: 11,
              numberOfPerpetratorInjured: 6,
              valueOfPropertyDamage: 7000,
            },
          },
        ],
      };

      eventService.getAll().subscribe((res) => {
        expect(res).toEqual(events);
      });

      const req = httpMock.expectOne(`${BASE_URL}?page=0&size=100`);
      expect(req.request.method).toBe('GET');
      req.flush(events);
    });
  });
});
