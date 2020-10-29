import Country from 'src/app/country/models/country.model';
import Province from 'src/app/province/models/province.model';

import City from '../../models/city.model';
import * as CityActions from '../city.actions';
import cityReducer from '../city.reducer';
import CityStoreState from '../city.store.state';

const cities = [
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
];

const initialState: CityStoreState = {
  cities: [],
};

const initialStateWithCities: CityStoreState = {
  cities,
};

describe('cityReducer', () => {
  describe('CityActions.setCities', () => {
    it('should store cities', () => {
      const action = CityActions.setCities({ cities });
      const actualState = cityReducer(initialState, action);
      const expectedState = { ...initialStateWithCities };

      expect(actualState).toEqual(expectedState);
      expect(actualState.cities.length).toBe(2);
    });

    it('should store empty cities list', () => {
      const emptyCitiesList = [];
      const action = CityActions.setCities({ cities: emptyCitiesList });
      const actualState = cityReducer(initialState, action);
      const expectedState = { ...initialState, cities: emptyCitiesList };

      expect(actualState).toEqual(expectedState);
      expect(actualState.cities.length).toBe(0);
    });
  });

  describe('CityActions.resetCities', () => {
    it('should reset cities list', () => {
      const action = CityActions.resetCities();
      const actualState = cityReducer(initialStateWithCities, action);
      const expectedState = { ...initialState };

      expect(actualState).toEqual(expectedState);
      expect(actualState.cities.length).toBe(0);
    });
  });
});
