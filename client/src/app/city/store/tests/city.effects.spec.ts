import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { of, ReplaySubject } from 'rxjs';
import Country from 'src/app/country/models/country.model';
import Province from 'src/app/province/models/province.model';

import CitiesGetResponse from '../../models/cities-get-response.model';
import City from '../../models/city.model';
import CityService from '../../services/city.service';
import * as CityActions from '../city.actions';
import CityEffects from '../city.effects';

const mockCities: CitiesGetResponse = {
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

describe('CityEffects', () => {
  let cityEffects: CityEffects;
  let actions$: ReplaySubject<any>;
  let cityService: CityService;

  beforeEach(() =>
    TestBed.configureTestingModule({
      providers: [
        CityEffects,
        provideMockActions(() => actions$),
        {
          provide: CityService,
          useValue: jasmine.createSpyObj('cityService', ['getAll']),
        },
      ],
    })
  );

  beforeEach(() => {
    cityEffects = TestBed.inject(CityEffects);
    cityService = TestBed.inject(CityService);
  });

  describe('fetchCities$', () => {
    beforeEach(() => {
      actions$ = new ReplaySubject(1);
      actions$.next(CityActions.fetchCities());
      (cityService.getAll as jasmine.Spy).and.returnValue(of(mockCities));
    });

    it('should return a setCities action', () => {
      cityEffects.fetchCities$.subscribe((resultAction) => {
        expect(resultAction).toEqual(
          CityActions.setCities({ cities: mockCities.content })
        );
        expect(cityService.getAll).toHaveBeenCalled();
      });
    });
  });
});
