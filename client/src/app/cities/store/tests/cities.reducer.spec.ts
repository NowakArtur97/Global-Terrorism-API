import City from '../../models/city.model';
import * as CityActions from '../cities.actions';
import citiesReducer from '../cities.reducer';
import CitiesStoreState from '../cities.store.state';

const initialState: CitiesStoreState = {
  cities: [],
};

describe('citiesReducer', () => {
  describe('CityActions.setCities', () => {
    it('should store cities', () => {
      const cities = [new City(1, 'city1', 10, 12)];
      const action = CityActions.setCities({ cities });
      const actualState = citiesReducer(initialState, action);
      const expectedState = { ...initialState, cities };

      expect(actualState).toEqual(expectedState);
    });

    it('should store empty cities list', () => {
      const cities = [];
      const action = CityActions.setCities({ cities });
      const actualState = citiesReducer(initialState, action);
      const expectedState = { ...initialState, cities };

      expect(actualState).toEqual(expectedState);
      expect(actualState.cities.length).toBe(0);
    });
  });

  describe('CityActions.resetCities', () => {
    it('should reset cities list', () => {
      const cities = [];
      const action = CityActions.resetCities();
      const actualState = citiesReducer(initialState, action);
      const expectedState = { ...initialState, cities };

      expect(actualState).toEqual(expectedState);
      expect(actualState.cities.length).toBe(0);
    });
  });
});
