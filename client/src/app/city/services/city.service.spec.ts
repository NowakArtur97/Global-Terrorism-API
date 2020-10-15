import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { getTestBed, TestBed } from '@angular/core/testing';

import CitiesGetResponse from '../models/cities-get-response.model';
import City from '../models/city.model';
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
        content: [new City(1, 'city1', 10, 12), new City(2, 'city2', 20, 22)],
      };

      citService.getCities().subscribe((res) => {
        expect(res).toEqual(cities);
      });

      const req = httpMock.expectOne(`${BASE_URL}?page=0&size=50`);
      expect(req.request.method).toBe('GET');
      req.flush(cities);
    });
  });
});
