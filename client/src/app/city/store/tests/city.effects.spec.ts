import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { of, ReplaySubject } from 'rxjs';

import CitiesGetResponse from '../../models/cities-get-response.model';
import CityService from '../../services/city.service';
import * as CityActions from '../city.actions';
import CityEffects from '../city.effects';

const mockCities: CitiesGetResponse = {
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
