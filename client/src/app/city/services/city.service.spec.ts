import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { getTestBed, TestBed } from '@angular/core/testing';

import CitiesGetResponse from '../models/cities-get-response.model';
import CityService from './city.service';

describe('cityService', () => {
  let injector: TestBed;
  let citService: CityService;
  let httpMock: HttpTestingController;

  const BASE_URL = 'http://localhost:8080/api/v1/cities';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CityService],
    });
  });

  beforeEach(() => {
    injector = getTestBed();
    citService = injector.inject(CityService);
    httpMock = injector.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('when get cities', () => {
    it('should return cities', () => {
      const cities: CitiesGetResponse = {
        content: [
          {
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
          {
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
        ],
      };

      citService.getAll().subscribe((res) => {
        expect(res).toEqual(cities);
      });

      const req = httpMock.expectOne(`${BASE_URL}?page=0&size=1000`);
      expect(req.request.method).toBe('GET');
      req.flush(cities);
    });
  });
});
