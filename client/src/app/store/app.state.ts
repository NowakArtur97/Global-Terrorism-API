import AuthStoreState from '../auth/store/auth.state';
import CityStoreState from '../city/store/city.state';
import EventStoreState from '../event/store/event.state';

export default interface AppStoreState {
  auth: AuthStoreState;
  city: CityStoreState;
  event: EventStoreState;
}
