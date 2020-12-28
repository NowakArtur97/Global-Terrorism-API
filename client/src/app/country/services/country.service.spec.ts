import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { getTestBed, TestBed } from '@angular/core/testing';

import CountriesGetResponse from '../models/countries-get-response.model';
import CountryService from './country.service';

describe('countryService', () => {
  let injector: TestBed;
  let countryService: CountryService;
  let httpMock: HttpTestingController;

  const BASE_URL = 'http://localhost:8080/api/v1/countries';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CountryService],
    });
  });

  beforeEach(() => {
    injector = getTestBed();
    countryService = injector.inject(CountryService);
    httpMock = injector.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('when get countries', () => {
    it('should return countries', () => {
      const countries: CountriesGetResponse = {
        content: [
          {
            id: 1,
            name: 'country',
          },
          {
            id: 2,
            name: 'country 2',
          },
        ],
      };

      countryService.getAll().subscribe((res) => {
        expect(res).toEqual(countries);
      });

      const req = httpMock.expectOne(`${BASE_URL}?page=0&size=200`);
      expect(req.request.method).toBe('GET');
      req.flush(countries);
    });
  });
});
