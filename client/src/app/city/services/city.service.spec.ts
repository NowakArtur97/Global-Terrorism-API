import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { getTestBed, TestBed } from '@angular/core/testing';
import Country from 'src/app/country/models/country.model';
import Province from 'src/app/province/models/province.model';

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
        content: [
          new City(
            3,
            'city',
            20,
            10,
            new Province(2, 'province', new Country(1, 'country'))
          ),
          new City(
            6,
            'city',
            10,
            30,
            new Province(5, 'province 2', new Country(4, 'country 2'))
          ),
        ],
      };

      citService.getAll().subscribe((res) => {
        expect(res).toEqual(cities);
      });

      const req = httpMock.expectOne(`${BASE_URL}?page=0&size=100`);
      expect(req.request.method).toBe('GET');
      req.flush(cities);
    });
  });
});
