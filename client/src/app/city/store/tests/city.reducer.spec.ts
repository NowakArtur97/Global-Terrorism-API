import City from '../../models/city.model';
import * as CityActions from '../city.actions';
import cityReducer from '../city.reducer';
import CityStoreState from '../city.store.state';

const cities = [new City(1, 'city', 10, 20), new City(2, 'city2', 20, 10)];

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
