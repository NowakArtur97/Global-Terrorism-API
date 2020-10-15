import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { getTestBed, TestBed } from '@angular/core/testing';
import City from 'src/app/city/models/city.model';

import Event from '../models/event.model';
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
          new Event(
            2,
            'summary',
            'motive',
            new Date(),
            true,
            true,
            true,
            new City(1, 'city', 10, 30)
          ),
          new Event(
            4,
            'summary 2',
            'motive 2',
            new Date(),
            false,
            false,
            false,
            new City(3, 'city 2', 20, 10)
          ),
        ],
      };

      eventService.getEvents().subscribe((res) => {
        expect(res).toEqual(events);
      });

      const req = httpMock.expectOne(`${BASE_URL}?page=0&size=50`);
      expect(req.request.method).toBe('GET');
      req.flush(events);
    });
  });
});
