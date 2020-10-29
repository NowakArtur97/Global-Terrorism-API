import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { getTestBed, TestBed } from '@angular/core/testing';
import City from 'src/app/city/models/city.model';
import Target from 'src/app/target/models/target.model';

import Victim from '../../victim/models/victim.model';
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
