import * as fromAuth from '../auth/store/auth.reducer';
import * as fromCity from '../city/store/city.reducer';
import * as fromEvent from '../event/store/event.reducer';

export default interface AppStoreState {
  auth: fromAuth.AuthStoreState;
  city: fromCity.CityStoreState;
  event: fromEvent.EventStoreState;
}
