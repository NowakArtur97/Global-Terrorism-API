import { Dictionary, EntityState } from '@ngrx/entity';

import City from '../../models/city.model';
import * as CityActions from '../city.actions';
import cityReducer from '../city.reducer';

const city1 = {
  id: 4,
  name: 'city',
  latitude: 20,
  longitude: 10,
  province: {
    id: 2,
    name: 'province',
    country: { id: 1, name: 'country' },
  },
};
const city2 = {
  id: 10,
  name: 'city 2',
  latitude: 10,
  longitude: 20,
  province: {
    id: 8,
    name: 'province 2',
    country: { id: 7, name: 'country 2' },
  },
};
const cities = [city1, city2];
const citiesDictionary: Dictionary<City> = {
  4: city1,
  10: city2,
};
const initialState: EntityState<City> = {
  ids: [],
  entities: {},
};

const initialStateWithCities: EntityState<City> = {
  ids: [4, 10],
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
