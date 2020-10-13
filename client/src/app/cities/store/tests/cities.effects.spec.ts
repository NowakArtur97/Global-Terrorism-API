import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { of, ReplaySubject } from 'rxjs';

import CitiesGetResponse from '../../models/cities-get-response.model';
import City from '../../models/city.model';
import CitiesService from '../../services/cities.service';
import * as CitiesActions from '../cities.actions';
import CitiesEffects from '../cities.effects';

const mockCities: CitiesGetResponse = {
  content: [new City(1, 'city1', 10, 12), new City(2, 'city2', 20, 22)],
};

describe('CitiesEffects', () => {
  let citiesEffects: CitiesEffects;
  let actions$: ReplaySubject<any>;
  let citiesService: CitiesService;

  beforeEach(() =>
    TestBed.configureTestingModule({
      providers: [
        CitiesEffects,
        provideMockActions(() => actions$),
        {
          provide: CitiesService,
          useValue: jasmine.createSpyObj('citiesService', ['getCities']),
        },
      ],
    })
  );

  beforeEach(() => {
    citiesEffects = TestBed.inject(CitiesEffects);
    citiesService = TestBed.inject(CitiesService);
  });

  describe('fetchCities$', () => {
    beforeEach(() => {
      actions$ = new ReplaySubject(1);
      actions$.next(CitiesActions.fetchCities());
      (citiesService.getCities as jasmine.Spy).and.returnValue(of(mockCities));
    });

    it('should return a setCities action', () => {
      citiesEffects.fetchCities$.subscribe((resultAction) => {
        expect(resultAction).toEqual(
          CitiesActions.setCities({ cities: mockCities.content })
        );
        expect(citiesService.getCities).toHaveBeenCalled();
      });
    });
  });
});
