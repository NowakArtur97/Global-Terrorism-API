import AuthStoreState from '../auth/store/auth.state';
import * as fromCity from '../city/store/city.reducer';
import * as fromEvent from '../event/store/event.reducer';

export default interface AppStoreState {
  auth: AuthStoreState;
  city: fromCity.CityStoreState;
  event: fromEvent.EventStoreState;
}
