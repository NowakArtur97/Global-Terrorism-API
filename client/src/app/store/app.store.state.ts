import AuthStoreState from '../auth/store/auth.store.state';
import CitiesStoreState from '../cities/store/cities.store.state';

export default interface AppStoreState {
  cities: CitiesStoreState;
  auth: AuthStoreState;
}
