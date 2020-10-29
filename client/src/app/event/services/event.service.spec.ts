import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { getTestBed, TestBed } from '@angular/core/testing';
import City from 'src/app/city/models/city.model';
import Country from 'src/app/country/models/country.model';
import Province from 'src/app/province/models/province.model';
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
