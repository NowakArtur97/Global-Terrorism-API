import AuthStoreState from '../auth/store/auth.store.state';
import CityStoreState from '../city/store/city.store.state';
import EventStoreState from '../event/store/event.store.state';

export default interface AppStoreState {
  auth: AuthStoreState;
  city: CityStoreState;
  event: EventStoreState;
}
