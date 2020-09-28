import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { getTestBed, TestBed } from '@angular/core/testing';

import CitiesGetResponse from '../models/cities-get-response.model';
import City from '../models/city.model';
import CitiesService from './cities.service';

describe('citiesService', () => {
  let injector: TestBed;
  let citiesService: CitiesService;
  let httpMock: HttpTestingController;

  const BASE_URL = 'http://localhost:8080/api/v1/cities';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CitiesService],
    });
  });

  beforeEach(() => {
    injector = getTestBed();
    citiesService = injector.inject(CitiesService);
    httpMock = injector.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('getCities$', () => {
    it('should return cities', () => {
      const cities: CitiesGetResponse = {
        content: [new City(1, 'city1', 10, 12), new City(2, 'city2', 20, 22)],
      };

      citiesService.getCities().subscribe((res) => {
        expect(res).toEqual(cities);
      });

      const req = httpMock.expectOne(`${BASE_URL}?page=0&size=50`);
      expect(req.request.method).toBe('GET');
      req.flush(cities);
    });
  });
});
