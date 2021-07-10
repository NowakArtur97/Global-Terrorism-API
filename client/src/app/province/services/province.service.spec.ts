import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { getTestBed, TestBed } from '@angular/core/testing';

import ProvincesGetResponse from '../models/provinces-get-response.model';
import ProvinceService from './province.service';

describe('provinceService', () => {
  let injector: TestBed;
  let provinceService: ProvinceService;
  let httpMock: HttpTestingController;

  const BASE_URL = 'http://localhost:8080/api/v1/provinces';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ProvinceService],
    });
  });

  beforeEach(() => {
    injector = getTestBed();
    provinceService = injector.inject(ProvinceService);
    httpMock = injector.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('when get provinces', () => {
    it('should return provinces', () => {
      const provinces: ProvincesGetResponse = {
        content: [
          {
            id: 2,
            name: 'province',
            country: {
              id: 1,
              name: 'country',
            },
          },
          {
            id: 4,
            name: 'province 2',
            country: {
              id: 3,
              name: 'country 2',
            },
          },
        ],
      };

      provinceService.getAll().subscribe((res) => {
        expect(res).toEqual(provinces);
      });

      const req = httpMock.expectOne(`${BASE_URL}?page=0&size=1000`);
      expect(req.request.method).toBe('GET');
      req.flush(provinces);
    });
  });
});
