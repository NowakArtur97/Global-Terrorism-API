import { Dictionary, EntityState } from '@ngrx/entity';
import Country from 'src/app/country/models/country.model';
import Province from 'src/app/province/models/province.model';

import City from '../../models/city.model';
import * as CityActions from '../city.actions';
import cityReducer from '../city.reducer';

const city1 = new City(
  3,
  'city',
  20,
  10,
  new Province(2, 'province', new Country(1, 'country'))
);
const city2 = new City(
  6,
  'city',
  10,
  30,
  new Province(5, 'province 2', new Country(4, 'country 2'))
);
const cities = [city1, city2];
const citiesDictionary: Dictionary<City> = {
  3: city1,
  6: city2,
};
const initialState: EntityState<City> = {
  ids: [],
  entities: {},
};

const initialStateWithCities: EntityState<City> = {
  ids: [3, 6],
  entities: citiesDictionary,
};

describe('cityReducer', () => {
  describe('CityActions.setCities', () => {
    it('should store cities', () => {
      const action = CityActions.setCities({ cities });
      const actualState = cityReducer(initialState, action);
      const expectedState = { ...initialStateWithCities };

      expect(actualState).toEqual(expectedState);
      expect(actualState.ids.length).toBe(2);
    });

    it('should store empty cities list', () => {
      const emptyCitiesList = [];
      const action = CityActions.setCities({ cities: emptyCitiesList });
      const actualState = cityReducer(initialState, action);
      const expectedState = { ...initialState, entities: {} };

      expect(actualState).toEqual(expectedState);
      expect(actualState.ids.length).toBe(0);
    });
  });

  describe('CityActions.resetCities', () => {
    it('should reset cities list', () => {
      const action = CityActions.resetCities();
      const actualState = cityReducer(initialStateWithCities, action);
      const expectedState = { ...initialState };

      expect(actualState).toEqual(expectedState);
      expect(actualState.ids.length).toBe(0);
    });
  });
});
